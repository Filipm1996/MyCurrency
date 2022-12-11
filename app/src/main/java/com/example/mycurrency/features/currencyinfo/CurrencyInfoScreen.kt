package com.example.mycurrency.features.currencyinfo

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CurrencyInfoScreen(
    currencyToShow: Currency,
    viewModel: CurrencyInfoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dayFromToday = remember {
        mutableStateOf(0)
    }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
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
                            dayFromToday.value = dayFromToday.value + 1
                            viewModel.getCurrencyRateByName(
                                dayFromToday.value,
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
                                dayFromToday.value = dayFromToday.value - 1
                                viewModel.getCurrencyRateByName(
                                    dayFromToday.value,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currencyToShow.name,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Black
                )
                if (dateInDialog.value != LocalDate.now()) {
                    viewModel.currencyToShow.value?.let {
                        Text(
                            text = it.rate,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            color = Color.Black
                        )
                    }
                } else {
                    Text(
                        text = currencyToShow.rate,
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        color = Color.Black
                    )
                }
                if (viewModel.showGraph.value) {
                    val listOfPrices = transformList(viewModel.listForChart.toList())
                    val listOfDates = toDates()
                    val verticalStep = calculateVerticalStep(listOfPrices)
                    val yValues = yValuesCalculate(listOfPrices, verticalStep)
                    val points = (0..30).map {
                        it + 1
                    }
                    Graph(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(top = 50.dp),
                        xValues = points,
                        yValues = yValues,
                        points = listOfPrices,
                        dates = listOfDates,
                        paddingSpace = 16.dp,
                        verticalStep = verticalStep
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    onClick = {
                        viewModel.deleteMyCurrencyByName(currencyToShow)
                    }) {
                    Text(text = "Usuń z ulubionych")
                }
            }
        }
    }
}

fun yValuesCalculate(listOfPrices: List<Int>, verticalStep: Int): List<Int> {
    val min = listOfPrices.min() - (listOfPrices.min()*0.02)
    val yValues = (0..30).map {
        min.toInt() + it * verticalStep
    }
    return yValues
}

fun calculateVerticalStep(listOfPrices: List<Int>): Int {
    val min = listOfPrices.min() - (listOfPrices.min()*0.02)
    val max = listOfPrices.max() +(listOfPrices.max()*0.02)
    return (max - min).toInt() / 30
}

fun transformList(listForChart: List<Double>): List<Int> {
    val prices = listForChart
    val newPrices = mutableListOf<Int>()
    val step = listForChart.size / 30
    newPrices.add(prices.first().toInt())
    for (i in 1..28) {
        val mutlipleStep = step * i
        newPrices.add(prices[mutlipleStep].toInt())
    }
    newPrices.add(prices.last().toInt())
    return newPrices
}

fun toDates(): List<String> {
    val now = LocalDate.now()
    val listOfDates = mutableListOf<LocalDate>()
    for (i in 4 downTo 1) {
        listOfDates.add(now.minusDays(i.toLong()))
    }
    listOfDates.add(now)
    val newListOfDates = mutableListOf<String>()
    val formatter = DateTimeFormatter.ofPattern("MM/dd")
    listOfDates.forEach { day ->
        val formatted = day.format(formatter).toString()
        newListOfDates.add(formatted)
    }
    return newListOfDates
}

