package com.example.currencies.data.Retrofit.CoinGecko

import com.example.currencies.data.Retrofit.CoinGecko.AllRatesResponse.AllRatesResponse
import com.example.mycurrency.data.network.coinGecko.HistoricalRateResponse.HistoricalRateResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoAPI {

    @GET("coins/markets?vs_currency=pln&sparkline=false")
    suspend fun getRecordsFromCoinGecko(@Query("page") page: String): AllRatesResponse


    @GET("coins/{id}/market_chart/range?vs_currency=pln")
    suspend fun getSingleRecordFromCoinGeckoByTime(
        @Path("id") id: String,
        @Query("from") from: Long,
        @Query("to") to: Long
    ): HistoricalRateResponse
}