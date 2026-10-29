package com.example.apinew
// com.example.merged.LoginResponseObject.kt
// Merged from both LoginResponseObject classes — all fields from both
data class LoginResponseObject(
    val id: Int? = null,
    val loginName: String? = null,
    val password: String? = null,
    val deptName: String? = null,
    val location: String? = null,
    val creationDate: String? = null,
    val lastUpdateDate: String? = null,
    val ouId: Int? = null,
    val locId: Int? = null,
    // App 1 specific
    val attribute1: String? = null,
    val attribute2: String? = null,
    val attribute3: String? = null,
    val attribute4: String? = null,
    val userId: Int? = null,
    val emailId: String? = null,
    val dmsLoc: String? = null,
    val message: String? = null,
    // App 1 uses location_name
    val location_name: String? = null,
    // App 2 uses locationName (different JSON key!)
    val locationName: String? = null,
    // App 2 specific
    val userContact: String? = null,
    val userName: String? = null
)