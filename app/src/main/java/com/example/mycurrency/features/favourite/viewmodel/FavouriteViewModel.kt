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

    private val todaysDate = LocalDateTime.now()
    val listOfCurrenciesToDisplay = mutableStateListOf<Currency>()
    val loading = mutableStateOf(false)
    val error = mutableStateOf("")

    fun getMyAllCurrencies() = viewModelScope.launch {
        val listOfCurrencies = currencyDbRepository.getMyAllCurrencies()
        val isUpToDate = checkIfDateIsRight(listOfCurrencies)
        if (!isUpToDate) {
            listOfCurrencies.forEach { listOfCurrenciesToDisplay.add(it) }
        } else {
            val unixFrom = LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toInstant().epochSecond
            val unixTo =
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val responseFromNBP = networkRepository.getRecordsFromNBP()
            listOfCurrencies.forEach { currency ->
                if (currency.typeOfCurrency == "crypto") {
                    val responseFromCoinGecko = networkRepository.getSingleRecordFromCoinGecko(
                        currency,
                        unixFrom,
                        unixTo
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

    fun deleteMyCurrencyByName(currency: Currency) =
        CoroutineScope(Dispatchers.IO).launch {
            currencyDbRepository.deleteMyCurrencyByName(currency.name)
            listOfCurrenciesToDisplay.remove(currency)
        }

    private fun updateCurrency(currencyToAdd: Currency) {
        viewModelScope.launch {
            updateCurrency(currencyToAdd)
            currencyDbRepository.updateCurrency(
                currencyToAdd.name,
                todaysDate.toString(),
                currencyToAdd.rate
            )
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
            ChronoUnit.HOURS.between(todaysDate, convertToDate(listOfCurrencies[0].addDate)) >= 6
        } else {
            false
        }
    }

    private fun convertToDate(addDate: String): LocalDateTime {
        return LocalDateTime.parse(addDate, ISO_LOCAL_DATE_TIME)
    }
}