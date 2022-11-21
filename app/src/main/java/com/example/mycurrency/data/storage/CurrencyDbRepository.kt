package com.example.mycurrency.data.storage

import com.example.mycurrency.data.storage.entities.Currency

interface CurrencyDbRepository {

    suspend fun insertMyCurrency(currency: Currency)

    suspend fun getMyAllCurrencies(): List<Currency>

    fun deleteMyCurrencyByName (name : String)

    fun deleteMyCurrencies()

    suspend fun updateCurrency(name : String, date : String, rate: String)
}