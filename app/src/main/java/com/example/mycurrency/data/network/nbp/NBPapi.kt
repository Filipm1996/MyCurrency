package com.example.currencies.data.Retrofit.NBP

import com.example.currencies.data.Retrofit.NBP.getAllResponse.NBPresponse
import com.example.currencies.data.Retrofit.NBP.getSingleResponse.SingleResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NBPapi {

    @GET("api/exchangerates/tables/A/?format=json")
    suspend fun getNBPrecords(): NBPresponse

    @GET("api/exchangerates/rates/a/{name}/{date}/?format=json")
    suspend fun getSingleRecordByTime(@Path("name") name : String, @Path("date") date: String) : SingleResponse

    @GET("api/exchangerates/rates/a/{name}/?format=json")
    suspend fun getSingleRecord(@Path("name") name : String) : SingleResponse
}