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
            val attribute1 = obj.attribute1
            val ouId = obj.ouId
            val loginName = obj.loginName
            val location=obj.location
            val locId=obj.locId
            val location_name=obj.locationName
            val deptName=obj.deptName
            val userContact=obj.userContact
            val userName=obj.userName

            LoginResponse(code, obj, attribute1, ouId,loginName,location,locId,location_name,deptName,userContact,userName)
//            LoginResponse(code, obj, attribute1, ouId,loginName)

        } else {
            val obj = objElement.asString
            LoginResponse(code, obj, null, null, null.toString(), null,null,null,null,null,null)
//            LoginResponse(code, obj, null, null, null.toString())

        }
    }
}

