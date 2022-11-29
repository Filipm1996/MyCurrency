package com.example.mycurrency.data.storage

import com.example.mycurrency.data.storage.dao.CurrencyDao
import com.example.mycurrency.data.storage.entities.Currency
import javax.inject.Inject

class CurrencyDbRepositoryImpl @Inject constructor(
    private val nbpDao: CurrencyDao,
    private val cryptoDao: CurrencyDao,
    private val myDao: CurrencyDao
) : CurrencyDbRepository {

    // My Database

    override suspend fun insertMyCurrency(currency: Currency) = myDao.insertCurrency(currency)

    override suspend fun getMyAllCurrencies() = myDao.getAllCurrencies()

    override suspend fun deleteMyCurrencyByName(name: String) = myDao.deleteCurrencyByName(name)

    override suspend fun deleteMyCurrencies() = myDao.deleteAll()

    override suspend fun updateCurrency(name: String, date: String, rate: String) {
        myDao.updateRate(rate, date, name)
    }

    //nbp Database

    override suspend fun insertNBPCurrency(currency: Currency) = nbpDao.insertCurrency(currency)

    override suspend fun getNBPAllCurrencies() = nbpDao.getAllCurrencies()

    override suspend fun deleteNBPCurrencies() = nbpDao.deleteAll()


    // crypto Database

    override suspend fun insertCryptoCurrency(currency: Currency) =
        cryptoDao.insertCurrency(currency)

    override suspend fun getCryptoAllCurrencies() = cryptoDao.getAllCurrencies()

    override suspend fun deleteCryptoCurrencies() = cryptoDao.deleteAll()
}