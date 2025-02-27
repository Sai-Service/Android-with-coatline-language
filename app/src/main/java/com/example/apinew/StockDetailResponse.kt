package com.example.apinew

data class StockDetailsResponse(
    val obj: List<StockItem>
)

data class StockItem(
    val CHASSIS_NO: String,
    val VARIANT_DESC: String,
    val MODEL_DESC: String,
    val VIN: String,
    val DEALER_LOCATION: String,
    val COLOUR: String,
    val STOCK_TRF_NO: String,
    val ENGINE_NO: String,
    val VEH_STATUS: String,
    val STK_REMARK: String,
    val FUEL_DESC: String
)