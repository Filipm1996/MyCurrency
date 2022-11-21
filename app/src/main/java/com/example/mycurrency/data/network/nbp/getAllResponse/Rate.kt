package com.example.currencies.data.Retrofit.NBP.getAllResponse

data class Rate(
    val code: String,
    val currency: String,
    val mid: Double
)