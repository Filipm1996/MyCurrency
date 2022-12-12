package com.example.mycurrency.features.favourite.viewmodel

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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val currencyDbRepository: CurrencyDbRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {
    val currencyToShow = mutableStateOf<Currency?>(null)
    private val todaysDate = LocalDateTime.now()
    val listOfCurrenciesToDisplay = mutableStateListOf<Currency>()
    val loading = mutableStateOf(false)
    val error = mutableStateOf("")

    fun getMyAllCurrencies() = viewModelScope.launch {
        val listOfCurrencies = currencyDbRepository.getMyAllCurrencies()
        if (!checkIfDateIsRight(listOfCurrencies)) {
            listOfCurrencies.forEach { listOfCurrenciesToDisplay.add(it) }
        } else {
            val responseFromNBP = networkRepository.getRecordsFromNBP()
            listOfCurrencies.forEach { currency ->
                if (currency.typeOfCurrency == "crypto") {
                    val responseFromCoinGecko = networkRepository.getSingleRecordFromCoinGecko(
                        "1",
                        currency
                    )
                    when (responseFromCoinGecko) {
                        is Resource.Success -> {
                            updateCurrency(responseFromCoinGecko.data!!)
                        }
                        else -> throwError(responseFromCoinGecko.message)
                    }
                } else {
                    when (responseFromNBP) {
                        is Resource.Success -> {
                            val currencyToAdd = responseFromNBP.data?.findLast {
                                it.name == currency.name
                            }
                            if (currencyToAdd != null) {
                                updateCurrency(currencyToAdd)
                            }
                        }
                        else -> throwError(responseFromNBP.message)
                    }
                }
            }
        }
    }

    fun getSingleCurrency(name: String) = viewModelScope.launch {
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

    fun getCurrencyRateByName(localDate: LocalDate, currency: Currency) {
        viewModelScope.launch {
            if (currency.typeOfCurrency == "crypto") {
                val response =
                    networkRepository.getSingleRecordFromCoinGecko("1",currency)
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

    private fun updateCurrency(currencyToAdd: Currency) {
        viewModelScope.launch {
            currencyDbRepository.deleteMyCurrencyByName(currencyToAdd.name)
            currencyDbRepository.insertMyCurrency(currencyToAdd)
            listOfCurrenciesToDisplay.add(currencyToAdd)
        }
    }

    private fun throwError(newError: String?) {
        if (error.value.isBlank()) {
            error.value = newError ?: "Niespodziewany błąd"
        } else if (error.value != newError) {
            error.value = newError ?: "Niespodziewany błąd"
        }
    }

    private fun checkIfDateIsRight(listOfCurrencies: List<Currency>): Boolean {
        return if (listOfCurrencies.isNotEmpty()) {
            ChronoUnit.HOURS.between(
                convertToDate(listOfCurrencies[0].addDate),
                todaysDate) >= 6
        } else {
            false
        }
    }

    private fun convertToDate(addDate: String): LocalDateTime {
        return LocalDateTime.parse(addDate, ISO_LOCAL_DATE_TIME)
    }
}