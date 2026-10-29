//package com.example.apinew
//
//
//object ApiFile {
//
//       // ─── ERP App ───────────────────────────────────────────────
//       const val ERP_APP_URL = "http://182.72.0.216:7485/ErpAndroid/"        // production new
////  const val ERP_APP_URL = "http://115.242.10.85:7485/ErpAndroid/"       // production jio
////  const val ERP_APP_URL = "http://172.16.21.149:6101/ErpAndroid/"       // clone
////  const val ERP_APP_URL = "http://10.0.2.2:8081/"                       // localhost
//
//       // ─── Delivery App ──────────────────────────────────────────
//       const val DELIVERY_APP_URL = "http://182.72.0.216:7485/SaiVehicleDelivery/"        // production new
////  const val DELIVERY_APP_URL = "http://115.242.10.85:7485/SaiVehicleDelivery/"       // production jio
////  const val DELIVERY_APP_URL = "http://172.16.21.149:6101/SaiVehicleDelivery/"       // clone
////  const val DELIVERY_APP_URL = "http://10.0.2.2:8081/"                              // localhost
//
//}
//
////ALSO CHANGE THE IP FROM network_security_config.xml file





package com.example.apinew

object ApiFile {

       // ── ERP ──────────────────────────────────────────────────
       // Only host + port — path is defined in ErpUserService @POST
//       const val ERP_APP_URL = "http://182.72.0.216:7485/ErpAndroid/"
//       const val ERP_LOGIN_URL= "http://182.72.0.216:7485/"
//       const val ERP_APP_URL = "http://172.16.21.149:6101/ErpAndroid/"
//       const val ERP_LOGIN_URL= "http://172.16.21.149:6101/"
//  const val ERP_APP_URL = "http://115.242.10.85:7485/"
//  const val ERP_APP_URL = "http://172.16.21.149:6101/"
  const val ERP_APP_URL = "http://10.0.2.2:8081/"
       const val ERP_LOGIN_URL= "http://10.0.2.2:8081/"

//    const val ERP_APP_URL = "https://nexgenapp.saihorizon.org:7585/ErpAndroid"
//    const val ERP_LOGIN_URL= "https://nexgenapp.saihorizon.org:7585/"

       // ── Delivery  ─────────────────────────────────────────────
       // Same server, different port or same port — only host+port
//       const val DELIVERY_APP_URL = "http://182.72.0.216:7485/SaiVehicleDelivery/"
//       const val DELIVERY_LOGIN_URL = "http://182.72.0.216:7485/"
//       const val DELIVERY_APP_URL = "http://172.16.21.149:6101/SaiVehicleDelivery/"
//       const val DELIVERY_LOGIN_URL = "http://172.16.21.149:6101/"
//  const val DELIVERY_APP_URL = "http://115.242.10.85:7485/"
//  const val DELIVERY_APP_URL = "http://172.16.21.149:6101/"
  const val DELIVERY_APP_URL = "http://10.0.2.2:8081/"
    const val DELIVERY_LOGIN_URL = "http://10.0.2.2:8081/"

//    const val DELIVERY_APP_URL = "https://nexgenapp.saihorizon.org:7585/SaiVehicleDelivery"
//    const val DELIVERY_LOGIN_URL = "https://nexgenapp.saihorizon.org:7585/"
}