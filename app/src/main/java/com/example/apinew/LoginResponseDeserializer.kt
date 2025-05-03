//package com.example.apinew
//
//import LoginResponse
//import LoginResponseObject
//import com.google.gson.*
//import java.lang.reflect.Type
//
//
//class LoginResponseDeserializer : JsonDeserializer<LoginResponse> {
//    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LoginResponse {
//        val jsonObject = json.asJsonObject
//
//        val code = jsonObject.get("code").asInt
//        val objElement = jsonObject.get("obj")
//
//        return if (objElement.isJsonObject) {
//            val obj = context.deserialize<LoginResponseObject>(objElement, LoginResponseObject::class.java)
////            val attribute1 = obj.attribute1
//            val ouId = obj.ouId
//            val loginName = obj.loginName
//            val location=obj.locName
//            val locId=obj.locId
////            val location_name=obj.location_name
//            val deptName=obj.deptName
//
//            LoginResponse(code, obj, ouId,loginName,location,locId,deptName)
////            LoginResponse(code, obj, attribute1, ouId,loginName)
//
//        } else {
//            val obj = objElement.asString
//            LoginResponse(code, obj, null, null.toString(), null.toString(), null,null)
////            LoginResponse(code, obj, null, null, null.toString())
//
//        }
//    }
//}
//



























package com.example.apinew

import LoginResponse
import LoginResponseObject
import com.google.gson.*
import java.lang.reflect.Type

class LoginResponseDeserializer : JsonDeserializer<LoginResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LoginResponse {
        val jsonObject = json.asJsonObject

        val code = jsonObject.get("code").asInt
        val objElement = jsonObject.get("obj")

        return if (objElement.isJsonObject) {
            val obj = context.deserialize<LoginResponseObject>(objElement, LoginResponseObject::class.java)

            LoginResponse(
                code = code,
                obj = obj.toString(),
                ouId = obj.ouId,
                loginName = obj.loginName,
                locName = obj.locName,
                locId = obj.locId,
                deptName = obj.deptName,
                role=obj.role
            )
        } else {
            val obj = objElement.asString
            LoginResponse(
                code = code,
                obj = obj,
                ouId = null,
                loginName = null.toString(),
                locName = null,
                locId = null,
                deptName = null,
                role=null
            )
        }
    }
}
