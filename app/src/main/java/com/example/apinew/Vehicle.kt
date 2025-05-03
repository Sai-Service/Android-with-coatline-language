package com.example.apinew

data class Vehicle(
    val CMNID: Int,
    val CMNDESC: String
) {
    override fun toString(): String {
        return CMNDESC
    }
}

