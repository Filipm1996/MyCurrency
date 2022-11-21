package com.example.mycurrency.data.storage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(tableName = "currencyTable")
data class Currency (
    val name : String,
    var shortName : String,
    val idCoingGecko : String? = null,
    var rate : String,
    val typeOfCurrency : String,
    val addDate : String,
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null
)

