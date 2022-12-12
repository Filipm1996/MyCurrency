package com.example.mycurrency.features.nbp.viewmodel

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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NbpViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val currencyDbRepository: CurrencyDbRepository
) : ViewModel() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val listOfCurrenciesToDisplay = mutableStateListOf<Currency>()
    val currencyToShow = mutableStateOf<Currency?>(null)
    val loading = mutableStateOf(false)
    val error = mutableStateOf("")

    fun insertMyCurrency(currency: Currency) = viewModelScope.launch {
        val listOfMyCurrencies = currencyDbRepository.getMyAllCurrencies()
        val item = listOfMyCurrencies.findLast { it.name == currency.name }
        if (item == null) {
            currencyDbRepository.insertMyCurrency(currency)
        } else {
            error.value = "Waluta jest juz dodana"
        }
    }

    fun getSingleRecordFromNBP(name: String) {
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

    private suspend fun getRecordsFromNBP() = networkRepository.getRecordsFromNBP()

    fun getAPIRecords() {
        viewModelScope.launch {
            loading.value = true
            val listFromDb = currencyDbRepository.getNBPAllCurrencies()
            if (checkValid(listFromDb)) {
                val response1 = getRecordsFromNBP()
                if (response1 is Resource.Error) {
                    error.value = response1.message ?: "error"
                    loading.value = false
                } else {
                    response1.data?.forEach {
                        currencyDbRepository.insertNBPCurrency(it)
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
    }

    fun getSingleRecordByDate(name: String, date: LocalDate) {
        viewModelScope.launch {
            val formattedDate = date.format(formatter)
            val response =
                networkRepository.getSingleRecordFromNBPByTime(name, formattedDate.toString())
            when (response) {
                is Resource.Success -> currencyToShow.value = response.data
                else -> {
                    currencyToShow.value!!.rate = ""
                    error.value = response.message ?: "error"
                }
            }
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
}