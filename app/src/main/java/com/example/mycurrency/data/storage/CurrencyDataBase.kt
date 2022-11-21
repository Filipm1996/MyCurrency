package com.example.currencies.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mycurrency.data.storage.dao.CurrencyDao
import com.example.mycurrency.data.storage.entities.Currency

@Database(
    entities =[Currency::class],
    version = 2,
    exportSchema = false
)

abstract class CurrencyDataBase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

}
