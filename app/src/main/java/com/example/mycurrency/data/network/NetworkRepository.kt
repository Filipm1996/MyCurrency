package com.example.mycurrency.data.network

import com.example.mycurrency.common.Resource
import com.example.mycurrency.data.storage.entities.Currency
import java.time.LocalDate

interface NetworkRepository {

    suspend fun getRecordsFromCoinGecko(page : String) : Resource<List<Currency>>

    suspend fun getSingleRecordFromCoinGecko(currency: Currency, from: Long, to : Long) : Resource<Currency>

    suspend fun getRecordsFromNBP() : Resource<List<Currency>>

    suspend fun getSingleRecordFromNBPByTime(name :String, date: String) : Resource<Currency>

    suspend fun getSingleRecordFromNBP (name:String) : Resource<Currency>
}