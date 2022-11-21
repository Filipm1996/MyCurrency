package com.example.mycurrency.data.storage

import com.example.mycurrency.data.storage.dao.CurrencyDao
import com.example.mycurrency.data.storage.entities.Currency
import javax.inject.Inject

class CurrencyDbRepositoryImpl @Inject constructor(private val dao: CurrencyDao) : CurrencyDbRepository {

    override suspend fun insertMyCurrency(currency: Currency) = dao.insertCurrency(currency)

    override suspend fun getMyAllCurrencies() = dao.getAllCurrencies()

    override fun deleteMyCurrencyByName(name: String) = dao.deleteCurrencyByName(name)

    override fun deleteMyCurrencies() = dao.deleteAll()

    override suspend fun updateCurrency(name: String, date: String, rate: String) {
        dao.updateRate(rate, date , name)
    }
}