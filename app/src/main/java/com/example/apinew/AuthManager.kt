package com.example.apinew

import android.util.Log
import kotlinx.coroutines.*

class AuthManager {

    var erpErrorMessage: String?      = null
    var erpIsRateLimited: Boolean     = false
    var deliveryErrorMessage: String? = null

    sealed class AuthResult {
        data class Success(val session: UserSession)       : AuthResult()
        data class MergedSuccess(val session: UserSession) : AuthResult()
        data class RateLimited(val message: String)        : AuthResult()
        data class Failure(val message: String)            : AuthResult()
    }

    suspend fun login(username: String, password: String): AuthResult {
        erpErrorMessage      = null
        erpIsRateLimited     = false
        deliveryErrorMessage = null

        val request = LoginRequest().apply {
            loginName     = username
            this.password = password
        }

        return withContext(Dispatchers.IO) {
            val erpDeferred      = async { tryErpLogin(request) }
            val deliveryDeferred = async { tryDeliveryLogin(request) }

            val erpSession      = erpDeferred.await()
            val deliverySession = deliveryDeferred.await()

            Log.d("AuthManager", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            Log.d("AuthManager", "ERP session null?      → ${erpSession == null}")
            Log.d("AuthManager", "Delivery session null? → ${deliverySession == null}")
            Log.d("AuthManager", "ERP rate limited?      → $erpIsRateLimited")
            Log.d("AuthManager", "ERP error message?     → $erpErrorMessage")
            Log.d("AuthManager", "Delivery error msg?    → $deliveryErrorMessage")
            Log.d("AuthManager", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            when {
                erpIsRateLimited -> {
                    Log.d("AuthManager", "RESULT → RateLimited")
                    AuthResult.RateLimited(
                        erpErrorMessage ?: "Account locked. Please contact admin."
                    )
                }

                erpSession != null && deliverySession != null -> {
                    Log.d("AuthManager", "RESULT → MergedSuccess")
                    AuthResult.MergedSuccess(
                        UserSession(
                            loginName        = erpSession.loginName,
                            ouId             = erpSession.ouId,
                            location         = erpSession.location,
                            locId            = erpSession.locId,
                            locationName     = erpSession.locationName,
                            deptName         = "${erpSession.deptName} | ${deliverySession.deptName}",
                            source           = "both",
                            attribute1       = erpSession.attribute1,
                            attribute2       = erpSession.attribute2,
                            role             = erpSession.role,
                            emailId          = erpSession.emailId,
                            userContact      = deliverySession.userContact,
                            userName         = deliverySession.userName,
                            erpDeptName      = erpSession.deptName,
                            deliveryDeptName = deliverySession.deptName
                        )
                    )
                }

                erpSession != null -> {
                    Log.d("AuthManager", "RESULT → Success (ERP only)")
                    AuthResult.Success(erpSession)
                }

                deliverySession != null -> {
                    Log.d("AuthManager", "RESULT → Success (Delivery only)")
                    AuthResult.Success(deliverySession)
                }

                else -> {
                    Log.d("AuthManager", "RESULT → Failure")
                    // ERP message has attempt count — more useful than delivery message
                    AuthResult.Failure(
                        erpErrorMessage
                            ?: deliveryErrorMessage
                            ?: "Invalid username or password"
                    )
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    private suspend fun tryErpLogin(request: LoginRequest): UserSession? {
        return try {
            Log.d("AuthManager", "ERP → calling login")

            val response = ApiClientErp.getUserService().userLoginSuspend(request)

            Log.d("AuthManager", "ERP HTTP code  → ${response.code()}")
            Log.d("AuthManager", "ERP successful → ${response.isSuccessful}")

            if (!response.isSuccessful) {
                Log.w("AuthManager", "ERP not successful → ${response.code()}")
                return null
            }

            // ── Read body ONCE — never call response.body() again ─
            val body = response.body()

            Log.d("AuthManager", "ERP body null? → ${body == null}")

            if (body == null) {
                Log.e("AuthManager", "ERP: body is NULL — Gson parse failed")
                return null
            }

            Log.d("AuthManager", "ERP body.code  → ${body.code}")
            Log.d("AuthManager", "ERP body.msg   → ${body.message}")
            Log.d("AuthManager", "ERP body.obj   → ${body.obj}")

            // ── Handle non-200 codes first ─────────────────────────
            if (body.code != 200) {

                val msg = body.obj?.toString()
                    ?.takeIf { it.isNotBlank() }
                    ?: body.message?.trim()
                        ?.takeIf { it.isNotBlank() }
                    ?: "Invalid credentials"

                Log.w("AuthManager", "ERP non-200 code=${body.code} msg=$msg")
                erpErrorMessage = msg

                // Check if 429 OR locked message inside 400
                if (body.code == 429 ||
                    msg.contains("locked", ignoreCase = true) ||
                    msg.contains("Too many", ignoreCase = true)
                ) {
                    Log.w("AuthManager", "ERP: rate limited / locked")
                    erpIsRateLimited = true
                }

                return null
            }

            // ── Code 200 — validate obj ────────────────────────────
            val obj = body.obj
            if (obj == null) {
                Log.e("AuthManager", "ERP: obj is null after code 200")
                return null
            }

            Log.d("AuthManager", "ERP obj fields:")
            Log.d("AuthManager", "  loginName    = ${obj.loginName}")
            Log.d("AuthManager", "  ouId         = ${obj.ouId}")
            Log.d("AuthManager", "  location     = ${obj.location}")
            Log.d("AuthManager", "  locId        = ${obj.locId}")
            Log.d("AuthManager", "  locationName = ${obj.locationName}")
            Log.d("AuthManager", "  location_name= ${obj.location_name}")
            Log.d("AuthManager", "  deptName     = ${obj.deptName}")
            Log.d("AuthManager", "  attribute1   = ${obj.attribute1}")
            Log.d("AuthManager", "  attribute2   = ${obj.attribute2}")
            Log.d("AuthManager", "  emailId      = ${obj.emailId}")

            val locationName = obj.locationName ?: obj.location_name

            if (obj.attribute1 != null && obj.ouId     != null &&
                obj.location   != null && obj.locId    != null &&
                locationName   != null && obj.deptName != null
            ) {
                Log.d("AuthManager", "ERP: session created ✓")
                UserSession(
                    loginName        = obj.loginName ?: "",
                    ouId             = obj.ouId,
                    location         = obj.location,
                    locId            = obj.locId,
                    locationName     = locationName,
                    deptName         = obj.deptName,
                    source           = "erp",
                    attribute1       = obj.attribute1,
                    attribute2       = obj.attribute2,
                    role             = obj.role,
                    emailId          = obj.emailId,
                    erpDeptName      = obj.deptName,
                    deliveryDeptName = ""
                )
            } else {
                Log.e("AuthManager", "ERP: session NOT created — null fields:")
                if (obj.attribute1 == null) Log.e("AuthManager", "  ✗ attribute1")
                if (obj.ouId == null)       Log.e("AuthManager", "  ✗ ouId")
                if (obj.location == null)   Log.e("AuthManager", "  ✗ location")
                if (obj.locId == null)      Log.e("AuthManager", "  ✗ locId")
                if (locationName == null)   Log.e("AuthManager", "  ✗ locationName")
                if (obj.deptName == null)   Log.e("AuthManager", "  ✗ deptName")
                null
            }

        } catch (e: Exception) {
            Log.e("AuthManager", "ERP exception TYPE: ${e.javaClass.simpleName}")
            Log.e("AuthManager", "ERP exception MSG:  ${e.message}")
            Log.e("AuthManager", "ERP exception FULL: ", e)
            null
        }
    }

    // ─────────────────────────────────────────────────────────
    private suspend fun tryDeliveryLogin(request: LoginRequest): UserSession? {
        return try {
            Log.d("AuthManager", "Delivery → calling login")
            val response = ApiClientDelivery.getUserService().userLoginSuspend(request)

            Log.d("AuthManager", "Delivery HTTP code  → ${response.code()}")
            Log.d("AuthManager", "Delivery successful → ${response.isSuccessful}")

            if (!response.isSuccessful) {
                Log.w("AuthManager", "Delivery not successful → ${response.code()}")
                return null
            }

            // ── Read body ONCE ────────────────────────────────
            val body = response.body()

            Log.d("AuthManager", "Delivery body null? → ${body == null}")
            Log.d("AuthManager", "Delivery body code  → ${body?.code}")
            Log.d("AuthManager", "Delivery body msg   → ${body?.message}")
            Log.d("AuthManager", "Delivery body obj   → ${body?.obj}")

            if (body == null) {
                Log.e("AuthManager", "Delivery: body is NULL — Gson parse failed")
                return null
            }

            // ── Any non-200 = failed login ─────────────────────
            if (body.code != 200) {
                val msg = body.obj?.toString()
                    ?.takeIf { it.isNotBlank() }
                    ?: body.message?.trim()
                        ?.takeIf { it.isNotBlank() }
                    ?: "Invalid username or password"
                Log.w("AuthManager", "Delivery code ${body.code}: $msg")
                deliveryErrorMessage = msg
                return null
            }

            // ── Code 200 — validate obj fields ────────────────
            val obj = body.obj
            if (obj == null) {
                Log.e("AuthManager", "Delivery: obj is null after code 200")
                return null
            }

            Log.d("AuthManager", "Delivery obj fields:")
            Log.d("AuthManager", "  loginName    = ${obj.loginName}")
            Log.d("AuthManager", "  ouId         = ${obj.ouId}")
            Log.d("AuthManager", "  location     = ${obj.location}")
            Log.d("AuthManager", "  locId        = ${obj.locId}")
            Log.d("AuthManager", "  locationName = ${obj.locationName}")
            Log.d("AuthManager", "  location_name= ${obj.location_name}")
            Log.d("AuthManager", "  deptName     = ${obj.deptName}")
            Log.d("AuthManager", "  userContact  = ${obj.userContact}")
            Log.d("AuthManager", "  userName     = ${obj.userName}")

            val locationName = obj.locationName ?: obj.location_name

            if (obj.ouId        != null && obj.location    != null &&
                obj.locId       != null && locationName    != null &&
                obj.deptName    != null && obj.userContact != null &&
                obj.userName    != null
            ) {
                Log.d("AuthManager", "Delivery: session created ✓")
                UserSession(
                    loginName        = obj.loginName ?: "",
                    ouId             = obj.ouId,
                    location         = obj.location,
                    locId            = obj.locId,
                    locationName     = locationName,
                    deptName         = obj.deptName,
                    source           = "delivery",
                    userContact      = obj.userContact,
                    userName         = obj.userName,
                    erpDeptName      = "",
                    deliveryDeptName = obj.deptName
                )
            } else {
                Log.e("AuthManager", "Delivery: session NOT created — null fields:")
                if (obj.ouId == null)        Log.e("AuthManager", "  ✗ ouId")
                if (obj.location == null)    Log.e("AuthManager", "  ✗ location")
                if (obj.locId == null)       Log.e("AuthManager", "  ✗ locId")
                if (locationName == null)    Log.e("AuthManager", "  ✗ locationName")
                if (obj.deptName == null)    Log.e("AuthManager", "  ✗ deptName")
                if (obj.userContact == null) Log.e("AuthManager", "  ✗ userContact")
                if (obj.userName == null)    Log.e("AuthManager", "  ✗ userName")
                null
            }

        } catch (e: Exception) {
            Log.e("AuthManager", "Delivery exception TYPE: ${e.javaClass.simpleName}")
            Log.e("AuthManager", "Delivery exception MSG:  ${e.message}")
            Log.e("AuthManager", "Delivery exception FULL: ", e)
            null
        }
    }
}