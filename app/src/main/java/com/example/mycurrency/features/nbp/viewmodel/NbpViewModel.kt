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
        }else {
            error.value = "Waluta jest juz dodana"
        }
    }

    fun getSingleRecordFromNBP(name: String) {
        viewModelScope.launch {
            val currencyToFind = listOfCurrenciesToDisplay.find {
                it.name == name
            }
            if (currencyToFind != null) {
                listOfCurrenciesToDisplay.clear()
                listOfCurrenciesToDisplay.add(currencyToFind)
            } else {
                error.value = "No currency found"
            }
        }
    }

    private suspend fun getRecordsFromNBP() = networkRepository.getRecordsFromNBP()

    fun getAPIRecords() {
        viewModelScope.launch {
            loading.value = true
            val response1 = getRecordsFromNBP()
            if (response1 is Resource.Error) {
                error.value = response1.message ?: "error"
                loading.value = false
            } else {
                response1.data?.forEach {
                    if (!listOfCurrenciesToDisplay.contains(it)) {
                        listOfCurrenciesToDisplay.add(it)
                    }
                }
                loading.value = false
            }
        }
    }

    fun getSingleRecordByDate(name: String, date : LocalDate) {
        viewModelScope.launch {
            val formattedDate = date.format(formatter)
            val response = networkRepository.getSingleRecordFromNBPByTime(name, formattedDate.toString())
            when (response){
                is Resource.Success -> currencyToShow.value = response.data
                else -> error.value = response.message ?: "error"
            }
        }
    }

}