package com.example.currencies.data.Retrofit.NBP.getAllResponse

data class NBPresponseItem(
    val effectiveDate: String,
    val no: String,
    val rates: List<Rate>,
    val table: String
)