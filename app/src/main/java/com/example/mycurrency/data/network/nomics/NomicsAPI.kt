package com.example.currencies.data.Retrofit.Nomics

import retrofit2.http.GET

interface NomicsAPI {


    @GET("currencies/ticker?key=a9850b93b18cb57e672477ec76c03e6cbab4df32")
    suspend fun getRecordsFromNomics () : ArrayList<NomicsResponseItem>
}