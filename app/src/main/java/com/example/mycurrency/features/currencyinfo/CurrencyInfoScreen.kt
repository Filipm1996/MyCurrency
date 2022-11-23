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
import java.time.LocalDate

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
        if (viewModel.listForChart.isNotEmpty()) {
            Graph(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                viewModel.listForChart.keys.toList(),
                viewModel.listForChart.values.toList(),
                10.dp
            )
        }
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
        }
    }
}