package com.example.apinew

import android.content.Context

/**
 * Persists the logged-in [UserSession] to SharedPreferences so the user stays
 * logged in across app restarts / task kills, until logout() is explicitly called.
 */
object SessionManager {

    private const val PREFS_NAME = "session_prefs"
    private const val KEY_LOGGED_IN = "logged_in"

    private const val KEY_LOGIN_NAME = "loginName"
    private const val KEY_OU_ID = "ouId"
    private const val KEY_LOCATION = "location"
    private const val KEY_LOC_ID = "locId"
    private const val KEY_LOCATION_NAME = "locationName"
    private const val KEY_DEPT_NAME = "deptName"
    private const val KEY_SOURCE = "source"
    private const val KEY_ATTRIBUTE1 = "attribute1"
    private const val KEY_DMS_LOC = "dmsLoc"
    private const val KEY_ATTRIBUTE2 = "attribute2"
    private const val KEY_EMAIL_ID = "emailId"
    private const val KEY_ROLE = "role"
    private const val KEY_USER_CONTACT = "userContact"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_ERP_DEPT_NAME = "erpDeptName"
    private const val KEY_DELIVERY_DEPT_NAME = "deliveryDeptName"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(context: Context, session: UserSession) {
        prefs(context).edit().apply {
            putBoolean(KEY_LOGGED_IN, true)
            putString(KEY_LOGIN_NAME, session.loginName)
            putInt(KEY_OU_ID, session.ouId)
            putString(KEY_LOCATION, session.location)
            putInt(KEY_LOC_ID, session.locId)
            putString(KEY_LOCATION_NAME, session.locationName)
            putString(KEY_DEPT_NAME, session.deptName)
            putString(KEY_SOURCE, session.source)
            putString(KEY_ATTRIBUTE1, session.attribute1)
            putString(KEY_DMS_LOC, session.dmsLoc)
            putString(KEY_ATTRIBUTE2, session.attribute2)
            putString(KEY_EMAIL_ID, session.emailId)
            putString(KEY_ROLE, session.role)
            putString(KEY_USER_CONTACT, session.userContact)
            putString(KEY_USER_NAME, session.userName)
            putString(KEY_ERP_DEPT_NAME, session.erpDeptName)
            putString(KEY_DELIVERY_DEPT_NAME, session.deliveryDeptName)
            apply()
        }
    }

    fun getSession(context: Context): UserSession? {
        val p = prefs(context)
        if (!p.getBoolean(KEY_LOGGED_IN, false)) return null

        val loginName = p.getString(KEY_LOGIN_NAME, null) ?: return null
        val location = p.getString(KEY_LOCATION, null) ?: return null
        val locationName = p.getString(KEY_LOCATION_NAME, null) ?: return null
        val deptName = p.getString(KEY_DEPT_NAME, null) ?: return null
        val source = p.getString(KEY_SOURCE, null) ?: return null

        return UserSession(
            loginName = loginName,
            ouId = p.getInt(KEY_OU_ID, 0),
            location = location,
            locId = p.getInt(KEY_LOC_ID, 0),
            locationName = locationName,
            deptName = deptName,
            source = source,
            attribute1 = p.getString(KEY_ATTRIBUTE1, null),
            dmsLoc = p.getString(KEY_DMS_LOC, null),
            attribute2 = p.getString(KEY_ATTRIBUTE2, null),
            emailId = p.getString(KEY_EMAIL_ID, null),
            role = p.getString(KEY_ROLE, null),
            userContact = p.getString(KEY_USER_CONTACT, null),
            userName = p.getString(KEY_USER_NAME, null),
            erpDeptName = p.getString(KEY_ERP_DEPT_NAME, "") ?: "",
            deliveryDeptName = p.getString(KEY_DELIVERY_DEPT_NAME, "") ?: ""
        )
    }

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun clearSession(context: Context) {
        prefs(context).edit().clear().apply()
        DemoVehicleAlertScheduler.cancel(context)
    }
}
