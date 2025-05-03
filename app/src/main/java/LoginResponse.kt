////data class LoginResponse(
////    var loginName: String = "",
////    var password: String = "",
////    val code : Int,
////    val ouId: Int,
////    val attribute1:String,
////    val obj:LoginResponseObject
////)
////data class LoginResponseObject(
////    val id: Int,
////    val loginName: String,
////    val password: String,
////    val deptName: String?,
////    val location: String?,
////    val creationDate: String?,
////    val lastUpdateDate: String?,
////    val attribute1: String,
////    val attribute2: String,
////    val attribute3: String?,
////    val attribute4: String?,
////    val userId: Int?,
////    val emailId: String?,
////    val locId: Int?,
////    val dmsLoc: String?,
////    val ouId: Int
////)
////
////
////
////
////
////
////
////
////
////
//data class LoginResponse(
//    val code: Int,
//    val obj: Any?,
//    val attribute1: String? = null,
//    val loginName:String,
//    val location:String?=null,
//    val locId: Int?=null,
//    val ouId: Int? = null,
//    val location_name:String?=null,
//    val deptName:String?=null
//)
//
//data class LoginResponseObject(
//    val id: Int,
//    val loginName: String,
//    val password: String,
//    val deptName: String?,
//    val location: String?,
//    val creationDate: String?,
//    val lastUpdateDate: String?,
//    val attribute1: String,
//    val attribute2: String,
//    val attribute3: String?,
//    val attribute4: String?,
//    val userId: Int?,
//    val emailId: String?,
//    val dmsLoc: String?,
//    val ouId: Int,
//    val locId: Int?,
//    val location_name:String
//
//)
//
//
//


// Main response class
data class LoginResponse(
    val code: Int,
    val obj: String,
    val loginName: String,
    val locName: String?,
    val locId: Int? = null,
    val ouId: Int? = null,
    val deptName: String? = null,
    val role:String?=null
)

data class LoginResponseObject(
    val id: Int,
    val loginName: String,
    val password: String,
    val empId: Int?,
    val empName: String?,
    val role: String?,
    val deptName: String? = null,
    val location: String? = null,
    val creationDate: String? = null,
    val lastUpdateDate: String? = null,
    val attribute1: String? = null,
    val attribute2: String? = null,
    val attribute3: String? = null,
    val attribute4: String? = null,
    val userId: Int? = null,
    val emailId: String? = null,
//    val locId: Int? = null,
    val dmsLoc: String? = null,
    val locId: Int?,
    val ouId: Int,
//    val ouId: Int = 0,
    val locName: String
)
