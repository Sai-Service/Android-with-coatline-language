package com.example.apinew

import java.util.Date

object WorkshopWashingUrlManager {

    //For Workshop WashingReport.kt file
    fun getWashingReportUrl( // used in WorkshopWashingReport for  fetchUninvoice()
        ouId: Int,
        locId: Int,
        fromDate: String,
        toDate: String
    ): String {
        return "${ApiFile.APP_URL}/vehWashingReport/vehWashingReportByOu?ouId=$ouId&locId=$locId&fromDate=$fromDate&toDate=$toDate"
    }

    fun getCityUrl(): String { // used in WorkshopWashingReport for fetchCityData()
        val city="City"
        return "${ApiFile.APP_URL}/fndcom/cmnType?cmnType=$city"
    }

    fun postSendReportUrl(): String {  //used in WorkshopWashingReport for sendReport()
        return "${ApiFile.APP_URL}/vehWashingReport/sendVehWashingMainReportMail"
    }

    fun fetchEmailList(ouId:String,locId:String):String {
        return "${ApiFile.APP_URL}/washMailMaster/washMailByLocId?ouId=$ouId&locId=$locId"
    }

    fun getVehWashingMainReportUrl(ouId: Int, locId: Int, fromDate: String, toDate: String): String {
        val encodedFromDate = java.net.URLEncoder.encode(fromDate, "UTF-8")
        val encodedToDate = java.net.URLEncoder.encode(toDate, "UTF-8")
        return "${ApiFile.APP_URL}/vehWashingReport/vehWashingMainReport?ouId=$ouId&locId=$locId&fromDate=$encodedFromDate&toDate=$encodedToDate"
    }

/////////////////////////////////////////////////////////////////////////////////////////

    //For Workshop Washing Vehicle History
    fun getWashingVehicleHistory( // used in WorkshopWashingReport for fetchTransferListData()
        vehNo: String
    ): String {
        return "${ApiFile.APP_URL}/vehWashingReport/vehWashHistoryByRegNo?regNo=$vehNo"
    }

    /////////////////////////////////////////////////////////////////////////////////////////


    //For Workshop Washing Module
    fun fetchWashStageNumber():String{ //used for WorkshopWashingModule for fetchwashStageNumber()
        val cmn="WASHSTAGE"
        return "${ApiFile.APP_URL}/washingRegister/washStageByCmnType?cmnType=$cmn"
    }

    fun fetchwashStageType(cmnCode:String):String{ //used for WorkshopWashingModule for fetchwashStageType()
        return "${ApiFile.APP_URL}/washingRegister/washStageTypeByCmnCode?cmnCode=$cmnCode"
    }

    fun getdetailsForVehicleInFirstTime(vehNo:String):String{ //used for WorkshopWashingModule for detailsForVehicleInFirstTime()
        return "${ApiFile.APP_URL}/washingRegister/vehDetailsForWash?regNo=$vehNo"
    }

    fun getdetailsForVehicleOutFirstTime(vehNo:String):String{
        return "${ApiFile.APP_URL}/washingRegister/vehDetailsForWashOut?regNo=$vehNo"
    }


}
