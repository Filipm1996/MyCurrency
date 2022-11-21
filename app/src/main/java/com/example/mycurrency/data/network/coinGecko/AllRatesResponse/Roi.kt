package com.example.currencies.data.Retrofit.CoinGecko.AllRatesResponse

data class Roi(
    val currency: String,
    val percentage: Double,
    val times: Double
)