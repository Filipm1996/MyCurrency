package com.example.mycurrency.features

import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycurrency.R
import com.example.mycurrency.data.storage.entities.Currency
import com.example.mycurrency.features.crypto.viewmodel.CryptoViewModel
import com.example.mycurrency.ui.theme.CardItem
import com.example.mycurrency.ui.theme.ShowError
import java.time.LocalDate

@Composable
fun CryptoScreen(
    viewModel: CryptoViewModel = hiltViewModel()
) {
    val showAddCurrencyDialogState = remember {
        mutableStateOf(false)
    }
    val currencyToShow = viewModel.currencyToShow
    val dateInDialog = remember {
        mutableStateOf(LocalDate.now())
    }
    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    val listOfCurrencies = viewModel.listOfCurrenciesToDisplay
    val loading = viewModel.loading
    val error = viewModel.error

    LaunchedEffect(Unit) {
        viewModel.getRecordsFromCoinGecko()
    }

    if (error.value.isNotEmpty()) {
        ShowError(error.value, context)
        error.value = ""
    }
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painterResource(id = R.drawable.dolar_background),
            contentDescription = "dolar background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                TextField(modifier = Modifier
                    .weight(1f),
                    value = text,
                    textStyle = TextStyle.Default.copy(fontSize = 15.sp, color = Color.White),
                    label = { Text("Wyszukaj walute", color = Color.LightGray) },
                    onValueChange = {
                        text = it
                    })
                Icon(
                    modifier = Modifier
                        .padding(
                            end = 10.dp, start = 10.dp,
                            top = 5.dp
                        )
                        .clickable {
                            if (text.isEmpty()) {
                                Toast
                                    .makeText(context, "Wpisz swoją walutę", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                viewModel.getSingleRecordFromCoinGecko(text)
                            }
                        },
                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                    tint = Color.White,
                    contentDescription = "search_icon"
                )
            }
            LazyColumn(
                modifier = Modifier.padding(bottom = 60.dp),
                state = rememberLazyListState()
            ) {
                items(listOfCurrencies.size) {
                    val item = listOfCurrencies[it]
                    CardItem(item) {
                        currencyToShow.value = it
                        showAddCurrencyDialogState.value = true
                    }
                }
            }
        }
        if (loading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .then(Modifier.size(100.dp))
                    .align(Alignment.Center)
            )
        }
    }
    if (showAddCurrencyDialogState.value && currencyToShow.value != null) {
        AlertDialog(
            backgroundColor = Color.LightGray,
            onDismissRequest = {
                showAddCurrencyDialogState.value = false
                dateInDialog.value = LocalDate.now()
            },
            text = {
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
                                    viewModel.getRecordFromCoinGeckoByDate(
                                        dateInDialog.value,
                                        currencyToShow.value!!
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
                                        viewModel.getRecordFromCoinGeckoByDate(
                                            dateInDialog.value,
                                            currencyToShow.value!!
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
                            text = currencyToShow.value!!.name,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Color.Black
                        )

                        Text(
                            text = currencyToShow.value!!.rate,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            color = Color.Black
                        )
                    }
                }

            },
            buttons = {
                Column(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showAddCurrencyDialogState.value = false }
                    ) {
                        Text("Zamknij")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.insertMyCurrency(currencyToShow.value!!)
                            showAddCurrencyDialogState.value = false
                        }
                    ) {
                        Text("Dodaj do ulubionych")
                    }
                }
            }
        )
    }
}

