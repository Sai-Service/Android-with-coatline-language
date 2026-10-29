package com.example.apinew

data class UserSession(
    val loginName: String,
    val ouId: Int,
    val location: String,
    val locId: Int,
    val locationName: String,
    val deptName: String,
    val source: String,              // "erp" | "delivery" | "both"
    // ERP-only
    val attribute1: String?  = null,
    val dmsLoc: String?  = null,
    val attribute2: String?  = null,
    val emailId: String?     = null,
    val role:String? = null,
    // Delivery-only
    val userContact: String? = null,
    val userName: String?    = null,
    // Conflict case — kept separately for Controller routing
    val erpDeptName: String      = deptName,
    val deliveryDeptName: String = ""
)