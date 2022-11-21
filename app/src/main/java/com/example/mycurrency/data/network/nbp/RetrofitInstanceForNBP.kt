package com.example.currencies.data.Retrofit.NBP


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceForNBP {

    val api : NBPapi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nbp.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NBPapi::class.java)
    }
}