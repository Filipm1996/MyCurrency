package com.example.mycurrency.ui.theme.navigation

sealed class Screen(val route : String){
    object NBPScreen : Screen ("nbp")
    object CryptoSceen : Screen("crypto")
    object MySceen : Screen ("my")
    object InfoScreen : Screen ("info/currency={currency}")
}
