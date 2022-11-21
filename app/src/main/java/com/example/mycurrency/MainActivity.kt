package com.example.mycurrency

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.example.mycurrency.ui.theme.MyCurrencyTheme
import com.example.mycurrency.ui.theme.navigation.BottomNavItem
import com.example.mycurrency.ui.theme.navigation.BottomNavigationBar
import com.example.mycurrency.ui.theme.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCurrencyTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            items = listOf(
                                BottomNavItem(
                                    name = "Cryptocurrencies",
                                    route = "crypto",
                                    icon = painterResource(id = R.drawable.bitcoin_btc_logo)
                                ),
                                BottomNavItem(
                                    name = "Favourites",
                                    route = "favourite",
                                    icon = painterResource(id = R.drawable.ic_baseline_person_24),
                                ),
                                BottomNavItem(
                                    name = "All NBP",
                                    route = "nbp",
                                    icon = painterResource(id = R.drawable.ic_baseline_list_24),
                                ),
                            ),
                            navController = navController,
                            onItemClick = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                ) {
                    Navigation(navController = navController)
                }
            }
        }
    }
}

