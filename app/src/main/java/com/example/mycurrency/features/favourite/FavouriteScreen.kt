package com.example.mycurrency.features

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycurrency.R
import com.example.mycurrency.data.storage.entities.Currency
import com.example.mycurrency.features.favourite.viewmodel.FavouriteViewModel
import com.example.mycurrency.ui.theme.CardItem
import com.example.mycurrency.ui.theme.ShowError

@Composable
fun FavouriteScreen(
    viewModel : FavouriteViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val showInfoCurrencyDialogState = remember {
            mutableStateOf(false)
        }
        val currencyToShow  = remember {
            mutableStateOf<Currency?>(null)
        }
        val context = LocalContext.current
        var text by rememberSaveable { mutableStateOf("") }
        val listOfCurrencies = viewModel.listOfCurrenciesToDisplay
        val loading = viewModel.loading
        val error = viewModel.error

        if(error.value.isNotEmpty()){
            ShowError(error.value,context)
            error.value = ""
        }
        LaunchedEffect(Unit){
            viewModel.getMyAllCurrencies()
        }
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
                                viewModel.getSingleCurrency(text)
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
                        showInfoCurrencyDialogState.value = true
                    }
                }
            }
        }
        if (loading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
            )
        }
    }
}