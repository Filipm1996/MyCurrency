package com.example.currencies.data.Retrofit.NBP.getSingleResponse

data class SingleResponse(
    val code: String,
    val currency: String,
    val rates: List<Rate>,
    val table: String
)