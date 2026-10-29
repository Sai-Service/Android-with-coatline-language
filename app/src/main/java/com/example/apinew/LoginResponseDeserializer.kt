//package com.example.apinew
//
////import LoginResponseObject
//// com.example.merged.LoginResponseDeserializer.kt
//import com.google.gson.*
//import java.lang.reflect.Type
//
//class LoginResponseDeserializer : JsonDeserializer<LoginResponse> {
//    override fun deserialize(
//        json: JsonElement,
//        typeOfT: Type,
//        context: JsonDeserializationContext
//    ): LoginResponse {
//        val jsonObject = json.asJsonObject
//        val code = jsonObject.get("code").asInt
//        val objElement = jsonObject.get("obj")
//
//        return if (objElement != null && objElement.isJsonObject) {
//            val obj = context.deserialize<LoginResponseObject>(
//                objElement,
//                LoginResponseObject::class.java
//            )
//
//            // location_name: App1 sends "location_name", App2 sends "locationName"
//            // We check both and use whichever is non-null
//            val resolvedLocationName = obj.location_name ?: obj.locationName
//
//            LoginResponse(
//                code         = code,
//                obj          = obj,
//                // App 1 fields
//                attribute1   = obj.attribute1,
//                attribute2   = obj.attribute2,
//                emailId      = obj.emailId,
//                message      = obj.message,
//                // App 2 fields
//                userContact  = obj.userContact,
//                userName     = obj.userName,
//                // Common fields
//                ouId         = obj.ouId,
//                loginName    = obj.loginName,
//                location     = obj.location,
//                locId        = obj.locId,
//                location_name = resolvedLocationName,
//                deptName     = obj.deptName
//                // source is set later by AuthManager, not from JSON
//            )
//        } else {
//            // obj is a plain string (error message like "Invalid credentials")
//            val objStr = if (objElement != null && !objElement.isJsonNull)
//                objElement.asString else null
//
//            LoginResponse(
//                code  = code,
//                obj   = objStr,
//                loginName = null
//            )
//        }
//    }
//}
//
