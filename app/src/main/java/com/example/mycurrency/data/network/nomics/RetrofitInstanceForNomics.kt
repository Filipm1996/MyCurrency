package com.example.currencies.data.Retrofit.Nomics

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceForNomics {

    val api : NomicsAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nomics.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NomicsAPI::class.java)
    }
}