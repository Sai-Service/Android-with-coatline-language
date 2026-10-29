package com.example.apinew

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

// Top-level wrapper — { "code": 200, "message": "...", "obj": { ... } }
data class LoginResponse(
    val code: Int?,
    val message: String?,
    val obj: LoginObj?,
//    val obj: JsonElement?
)

// Everything the server puts inside "obj" — covers both ERP and Delivery
data class LoginObj(
    val loginId: Int?,
    val loginName: String?,
    val password: String?,

    // Common fields
    val ouId: Int?,
    val location: String?,
    val locId: Int?,
    val deptName: String?,
    val emailId: String?,
    val role: String?,
    val dmsLoc: String?,

    // Server returns "locationName" in Delivery, "location_name" in ERP
    val locationName: String?,
    @SerializedName("location_name")
    val location_name: String?,

    // ERP-only
    val attribute1: String?,
    val attribute2: String?,
    val attribute3: String?,
    val attribute4: String?,
    val attribute5: String?,
    val userId: Int?,

    // Delivery-only
    val userName: String?,
    val userContact: String?
)

private fun JsonElement?.asStringOrNull(): String? {
    if (this == null || isJsonNull) return null
    return when {
        isJsonPrimitive -> asString.takeIf { it.isNotBlank() }
        isJsonObject    -> toString()
        else            -> null
    }
}