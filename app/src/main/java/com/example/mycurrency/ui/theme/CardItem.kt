package com.example.mycurrency.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.mycurrency.R
import com.example.mycurrency.data.storage.entities.Currency

@Composable
fun CardItem (
    currency: Currency,
    onClick : (currency: Currency) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            ,
        colors = CardDefaults.cardColors(
            contentColor = colorResource(id = R.color.transparent_grey),
            containerColor = colorResource(id = R.color.transparent_grey)
        ),
        shape = RoundedCornerShape(20.dp)
    ){
        Box(modifier = Modifier
            .clickable {
                onClick.invoke(currency)
            }
            .fillMaxWidth()
            .padding(15.dp))

        {
            Text(
                text = currency.name,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            Text(
                text = currency.rate,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }

}