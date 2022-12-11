package com.example.mycurrency.features.currencyinfo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycurrency.common.Resource
import com.example.mycurrency.data.network.NetworkRepository
import com.example.mycurrency.data.storage.CurrencyDbRepository
import com.example.mycurrency.data.storage.entities.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CurrencyInfoViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val currencyDbRepository: CurrencyDbRepository
) : ViewModel() {
    val currencyToShow = mutableStateOf<Currency?>(null)
    val showGraph = mutableStateOf(false)
    var listForChart = mutableStateListOf<Double>()
    val loading = mutableStateOf(false)
    val error = mutableStateOf("")

    fun getCryptoRecordsFrom5Days(currency: Currency) {
        if (listForChart.isEmpty()) {
            viewModelScope.launch {
                val response =
                    networkRepository.get5DaysRecordFromCoinGecko(currency)
                when (response) {
                    is Resource.Success -> {
                        response.data?.forEach { (_, price) ->
                            listForChart.add(price)
                        }
                        showGraph.value = true
                    }
                    else -> {
                        error.value = response.message ?: "error"
                    }
                }
            }
        }
    }

    fun getCurrencyRateByName(daysFromToday : Int,localDate: LocalDate, currency: Currency) {
        viewModelScope.launch {
            if (currency.typeOfCurrency == "crypto") {
                val response =
                    networkRepository.getSingleRecordFromCoinGecko(daysFromToday.toString(),currency)
                when (response) {
                    is Resource.Success -> {
                        currencyToShow.value = response.data
                    }
                    else -> {
                        error.value = response.message ?: "error"
                    }
                }
            } else {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formattedDate = localDate.format(formatter)
                val response =
                    networkRepository.getSingleRecordFromNBPByTime(
                        currency.shortName,
                        formattedDate.toString()
                    )
                when (response) {
                    is Resource.Success -> currencyToShow.value = response.data
                    else -> error.value = response.message ?: "error"
                }
            }
        }
    }

    fun deleteMyCurrencyByName(currency: Currency) =
        CoroutineScope(Dispatchers.IO).launch {
            currencyDbRepository.deleteMyCurrencyByName(currency.name)
        }
}