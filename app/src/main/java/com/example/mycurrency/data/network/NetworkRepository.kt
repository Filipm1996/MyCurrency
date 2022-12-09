package com.example.mycurrency.data.network

import com.example.mycurrency.common.Resource
import com.example.mycurrency.data.storage.entities.Currency

interface NetworkRepository {

    suspend fun getRecordsFromCoinGecko(page: String): Resource<List<Currency>>


    suspend fun getSingleRecordFromCoinGecko(
        day : String,
        currency: Currency,
    ): Resource<Currency>

    suspend fun get5DaysRecordFromCoinGecko(
        currency: Currency,
    ): Resource<Map<Double, Double>>

    suspend fun getRecordsFromNBP(): Resource<List<Currency>>

    suspend fun getSingleRecordFromNBPByTime(name: String, date: String): Resource<Currency>

    suspend fun getSingleRecordFromNBP(name: String): Resource<Currency>
}