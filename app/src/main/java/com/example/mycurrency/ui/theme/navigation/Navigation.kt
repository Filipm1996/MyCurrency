package com.example.mycurrency.ui.theme.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mycurrency.data.storage.entities.Currency
import com.example.mycurrency.features.CryptoScreen
import com.example.mycurrency.features.FavouriteScreen
import com.example.mycurrency.features.NbpScreen
import com.example.mycurrency.features.currencyinfo.CurrencyInfoScreen
import com.squareup.moshi.Moshi

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "nbp") {
        composable(Screen.CryptoSceen.route) {
            CryptoScreen()
        }
        composable(Screen.MySceen.route) {
            FavouriteScreen(navController = navController)
        }
        composable(Screen.NBPScreen.route) {
            NbpScreen()
        }
        composable(route = Screen.InfoScreen.route) { backStackEntry ->
            val currencyJson =  backStackEntry.arguments?.getString("currency")
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter(Currency::class.java).lenient()
            val currency = jsonAdapter.fromJson(currencyJson!!)
            CurrencyInfoScreen(currency!!)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = Color.DarkGray,
        elevation = 5.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Gray,
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if(selected){
                        if(item.badgeCount > 0) {
                            BadgedBox(
                                badge= {
                                    Text(
                                        text = item.badgeCount.toString(),
                                        color = Color.White
                                    )
                                }
                            ) {
                                Icon(
                                    painter = item.icon,
                                    contentDescription = item.name,
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        } else {
                                Icon(
                                    painter = item.icon,
                                    contentDescription = item.name,
                                    tint = Color.White
                                )
                                Text(
                                    text = item.name,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                        }
                    }else{
                            if(item.badgeCount > 0) {
                                BadgedBox(
                                    badge= {
                                        Text(text = item.badgeCount.toString())
                                    }
                                ) {
                                    Icon(
                                        painter = item.icon,
                                        contentDescription = item.name,
                                    )
                                }
                            } else {
                                Icon(
                                    painter = item.icon,
                                    contentDescription = item.name,
                                )
                            }
                    }
                    }
                }
            )
        }
    }
}
