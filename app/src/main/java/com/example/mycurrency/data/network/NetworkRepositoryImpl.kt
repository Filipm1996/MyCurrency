package com.example.mycurrency.data.network

import android.util.Log
import com.example.currencies.data.Retrofit.CoinGecko.CoinGeckoAPI
import com.example.currencies.data.Retrofit.NBP.NBPapi
import com.example.mycurrency.common.Resource
import com.example.mycurrency.data.storage.entities.Currency
import java.time.LocalDateTime
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val coinGeckoAPI: CoinGeckoAPI,
    private val nbPapi: NBPapi
) : NetworkRepository {

    private val todaysDate: LocalDateTime = LocalDateTime.now()

    override suspend fun getRecordsFromCoinGecko(page: String): Resource<List<Currency>> {
        val listOfCrypto = mutableListOf<Currency>()
        return try {
            val list = coinGeckoAPI.getRecordsFromCoinGecko(page)
            list.forEach {
                val price = String.format("%.4f", it.current_price)
                price.replace(",", ".")
                val currency = Currency(
                    name = it.name,
                    shortName = it.symbol,
                    idCoingGecko = it.id,
                    rate = price,
                    typeOfCurrency = "crypto",
                    addDate = todaysDate.toString()
                )
                listOfCrypto.add(currency)
            }
            Resource.Success(listOfCrypto)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }

    override suspend fun getSingleRecordFromCoinGecko(
        day : String,
        currency: Currency,
    ): Resource<Currency> {
        return try {
            println(day)
            val response =
                coinGeckoAPI.getSingleRecordFromCoinGeckoByTime(currency.idCoingGecko!!, "pln", day)
            val length = response.prices.size
            println(length)
            val price = String.format("%.4f", response.prices[0][1])
            price.replace(",", ".")
            val currency = Currency(
                name = currency.name,
                shortName = currency.shortName,
                idCoingGecko = currency.idCoingGecko,
                rate = price,
                typeOfCurrency = "crypto",
                addDate = todaysDate.toString()
            )
            Resource.Success(currency)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }

    override suspend fun get5DaysRecordFromCoinGecko(
        currency: Currency,
    ): Resource<Map<Double, Double>> {
        return try {
            val response =
                coinGeckoAPI.getSingleRecordFromCoinGeckoByTime(currency.idCoingGecko!!, "pln", "5")
            val newMapOfPrices = mutableMapOf<Double, Double>()
            response.prices.forEach {
                newMapOfPrices[it[0]] = it[1]
            }
            Resource.Success(newMapOfPrices)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }

    override suspend fun getRecordsFromNBP(): Resource<List<Currency>> {
        val listOfCurrencies = mutableListOf<Currency>()
        return try {
            val response = nbPapi.getNBPrecords()
            val listOfResponse = response[0].rates
            for (record in listOfResponse) {
                val price = String.format("%.4f", record.mid)
                val currency = Currency(
                    name = record.currency,
                    shortName = record.code,
                    rate = price,
                    typeOfCurrency = "normal",
                    addDate = todaysDate.toString()
                )
                listOfCurrencies.add(currency)
            }
            Resource.Success(listOfCurrencies)
        } catch (e: Exception) {
            Log.e("Exception", e.message!!)
            Resource.Error(e.message ?: "Error")
        }
    }

    override suspend fun getSingleRecordFromNBPByTime(
        name: String,
        date: String
    ): Resource<Currency> {
        return try {
            val response = nbPapi.getSingleRecordByTime(name, date)
            val currency = Currency(
                name = response.currency,
                shortName = response.code,
                rate = response.rates[0].mid.toString(),
                typeOfCurrency = "normal",
                addDate = todaysDate.toString()
            )
            Resource.Success(currency)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }

    override suspend fun getSingleRecordFromNBP(name: String): Resource<Currency> {
        return try {
            val response = nbPapi.getSingleRecord(name)
            val currency = Currency(
                name = response.currency,
                shortName = response.code,
                rate = response.rates[0].mid.toString(),
                typeOfCurrency = "normal",
                addDate = todaysDate.toString()
            )
            Resource.Success(currency)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }

}