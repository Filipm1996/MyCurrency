package com.example.mycurrency.data.network.coinGecko.HistoricalRateResponse

data class HistoricalRateResponse(
    val market_caps: List<List<Double>>,
    val prices: List<List<Double>>,
    val total_volumes: List<List<Double>>
)