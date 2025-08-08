package com.example.apinew
data class LoginResponseObject(
    val id: Int,
    val loginName: String,
    val password: String,
    val deptName: String?,
    val location: String?,
    val creationDate: String?,
    val lastUpdateDate: String?,
    val attribute1: String,
    val attribute2: String,
    val attribute3: String?,
    val attribute4: String?,
    val userId: Int?,
    val emailId: String?,
    val locId: Int?,
    val dmsLoc: String?,
    val ouId: Int,
    val location_name:String,
    val message:String
)