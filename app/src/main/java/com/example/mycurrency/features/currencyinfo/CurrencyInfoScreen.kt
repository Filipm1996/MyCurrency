package com.example.mycurrency.features.currencyinfo

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycurrency.R
import com.example.mycurrency.data.storage.entities.Currency
import com.example.mycurrency.features.currencyinfo.viewmodel.CurrencyInfoViewModel
import com.example.mycurrency.ui.theme.Graph
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CurrencyInfoScreen(
    currencyToShow: Currency,
    viewModel: CurrencyInfoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dateInDialog = remember {
        mutableStateOf(LocalDate.now())
    }
    LaunchedEffect(Unit) {
        viewModel.getCryptoRecordsFrom5Days(currencyToShow)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(
                            end = 10.dp, start = 10.dp,
                            top = 5.dp
                        )
                        .clickable {
                            dateInDialog.value = dateInDialog.value.minusDays(1)
                            viewModel.getCurrencyRateByName(
                                dateInDialog.value,
                                currencyToShow
                            )
                        },
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_back_ios_24),
                    contentDescription = "date back arrow"
                )
                Text(
                    text = dateInDialog.value.toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 33.sp,
                    color = Color.Black
                )
                Icon(
                    modifier = Modifier
                        .padding(
                            end = 10.dp, start = 10.dp,
                            top = 5.dp
                        )
                        .clickable {
                            if (dateInDialog.value != LocalDate.now()) {
                                dateInDialog.value = dateInDialog.value.plusDays(1)
                                viewModel.getCurrencyRateByName(
                                    dateInDialog.value,
                                    currencyToShow
                                )
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Nie znamy przyszłości :)",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        },
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
                    contentDescription = "date forward arrow"
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currencyToShow.name,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Black
                )

                Text(
                    text = currencyToShow.rate,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    color = Color.Black
                )
            }
            if (viewModel.showGraph.value) {
                val transformedLists = transformLists(viewModel.listForChart.toMap())
                val listOfPrices = transformedLists[0] as List<Int>
                val listOfDates = transformedLists[1] as List<String>
                val verticalStep = calculateVerticalStep(listOfPrices)
                val yValues = yValuesCalculate(listOfPrices,verticalStep)
                val points = (0..9).map {
                    it + 1
                }
                Graph(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(top = 20.dp),
                    xValues = points ,
                    yValues = yValues,
                    points = listOfPrices,
                    paddingSpace = 16.dp,
                    verticalStep = verticalStep
                )
            }
        }
    }
}

fun yValuesCalculate(listOfPrices: List<Int>, verticalStep: Int): List<Int> {
    val min = listOfPrices.min()
    val yValues = (1..10).map {
        min + it * verticalStep
    }
    return yValues
}

fun calculateVerticalStep(listOfPrices: List<Int>): Int {
    val min = listOfPrices.min()
    val max = listOfPrices.max()
    return (max - min)/10
}

fun transformLists(listForChart: Map<Double, Double>) : List<Any> {
    val prices = listForChart.values.toList()
    val dates = listForChart.keys.toList()
    val newPrices = mutableListOf<Int>()
    val stringDates = toDates(dates)
    val step = listForChart.size/10
    newPrices.add(prices.first().toInt())
    for(i in 1..8){
        val mutlipleStep = step * i
        newPrices.add(prices[mutlipleStep].toInt())
    }
    newPrices.add(prices.last().toInt())
    return listOf(newPrices,stringDates)
}

fun toDates(toList: List<Double>): List<String> {
    val newListOfDates = mutableListOf<String>()
    val formatter = DateTimeFormatter.ofPattern("MM/dd")
    toList.forEach {
        val dt = Instant.ofEpochSecond(it.toLong())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val formatted = dt.format(formatter).toString()
        newListOfDates.add(formatted)
    }
    return newListOfDates
}

