package com.example.mycurrency.features.crypto.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycurrency.common.Resource
import com.example.mycurrency.data.network.NetworkRepository
import com.example.mycurrency.data.storage.CurrencyDbRepository
import com.example.mycurrency.data.storage.entities.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val currencyDbRepository: CurrencyDbRepository
) : ViewModel() {
    val listOfCurrenciesToDisplay = mutableStateListOf<Currency>()
    val currencyToShow = mutableStateOf<Currency?>(null)
    val loading = mutableStateOf(false)
    val error = mutableStateOf("")

    fun getSingleRecordFromCoinGecko(name: String) {
        viewModelScope.launch {
            val currenciesToFind = listOfCurrenciesToDisplay.filter {
                it.name.contains(name, ignoreCase=true)
            }
            if (currenciesToFind.isNotEmpty()) {
                listOfCurrenciesToDisplay.clear()
                listOfCurrenciesToDisplay.addAll(currenciesToFind)
            } else {
                error.value = "No currency found"
            }
        }
    }

    fun insertMyCurrency(currency: Currency) = viewModelScope.launch {
        val listOfMyCurrencies = currencyDbRepository.getMyAllCurrencies()
        val item = listOfMyCurrencies.findLast { it.name == currency.name }
        if (item == null) {
            currencyDbRepository.insertMyCurrency(currency)
        } else {
            error.value = "Waluta jest juz dodana"
        }
    }

    fun getRecordsFromCoinGecko() =
        viewModelScope.launch {
            loading.value = true
            val listFromDb = currencyDbRepository.getCryptoAllCurrencies()
            if (checkValid(listFromDb)) {
                val response1 = networkRepository.getRecordsFromCoinGecko("0")
                if (response1 is Resource.Error) {
                    error.value = response1.message ?: "error"
                    loading.value = false
                } else {
                    response1.data?.forEach {
                        currencyDbRepository.insertCryptoCurrency(it)
                        if (!listOfCurrenciesToDisplay.contains(it)) {
                            listOfCurrenciesToDisplay.add(it)
                        }
                    }
                    loading.value = false
                }
            } else {
                listFromDb.forEach { listOfCurrenciesToDisplay.add(it) }
                loading.value = false
            }
        }

    private fun checkValid(listFromDb: List<Currency>): Boolean {
        val today = LocalDateTime.now()
        return if (listFromDb.isEmpty()) {
            true
        } else if (today.dayOfYear != LocalDateTime.parse(listFromDb[0].addDate).dayOfYear) {
            viewModelScope.launch {
                currencyDbRepository.deleteCryptoCurrencies()
            }
            true
        } else {
            false
        }
    }

    fun getRecordFromCoinGeckoByDate(daysFromToday: Int, currency: Currency) =
        viewModelScope.launch {
            val response =
                networkRepository.getSingleRecordFromCoinGecko(daysFromToday.toString(), currency)
            when (response) {
                is Resource.Success -> {
                    println(response.data?.rate ?: "nnull")
                    currencyToShow.value = response.data
                }
                else -> {
                    currencyToShow.value = null
                    error.value = response.message ?: "error"
                }
            }
        }
}