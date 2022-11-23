package com.example.mycurrency.features.currencyinfo.viewmodel

import androidx.compose.runtime.mutableStateMapOf
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CurrencyInfoViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val currencyDbRepository: CurrencyDbRepository
) : ViewModel() {
    val currencyToShow = mutableStateOf<Currency?>(null)
    var listForChart = mutableStateMapOf<Double,Double>()
    val loading = mutableStateOf(false)
    val error = mutableStateOf("")

    fun getCryptoRecordsFrom5Days(currency: Currency) {
        viewModelScope.launch {
            val date = LocalDate.now()
            val unixFrom =
                date.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val unixTo = date.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val response =
                networkRepository.get5DaysRecordFromCoinGecko(currency, unixFrom, unixTo)
            when (response) {
                is Resource.Success -> {
                    response.data?.forEach { (date, price) ->
                        listForChart[date] = price
                    }
                }
                else -> {
                    error.value = response.message ?: "error"
                }
            }
        }
    }

    fun getCurrencyRateByName(localDate: LocalDate, currency: Currency) {
        viewModelScope.launch {
            if (currency.typeOfCurrency == "crypto") {
                val unixFrom =
                    localDate.minusDays(1).atStartOfDay(ZoneId.systemDefault())
                        .toInstant().epochSecond
                val unixTo = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
                val response =
                    networkRepository.getSingleRecordFromCoinGecko(currency, unixFrom, unixTo)
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
}