package com.example.mycurrency.data.storage

import com.example.mycurrency.data.storage.entities.Currency

interface CurrencyDbRepository {

    suspend fun insertMyCurrency(currency: Currency)

    suspend fun getMyAllCurrencies(): List<Currency>

    fun deleteMyCurrencyByName (name : String)

    suspend fun deleteMyCurrencies()

    suspend fun updateCurrency(name : String, date : String, rate: String)

    suspend fun insertNBPCurrency(currency: Currency)

    suspend fun getNBPAllCurrencies(): List<Currency>

    suspend fun deleteNBPCurrencies()

    suspend fun insertCryptoCurrency(currency: Currency)

    suspend fun getCryptoAllCurrencies(): List<Currency>

    suspend fun deleteCryptoCurrencies()
}