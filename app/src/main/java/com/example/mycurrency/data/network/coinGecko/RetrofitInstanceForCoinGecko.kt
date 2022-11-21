package com.example.currencies.data.Retrofit.CoinGecko

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceForCoinGecko {
    val api: CoinGeckoAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoAPI::class.java)
    }
}