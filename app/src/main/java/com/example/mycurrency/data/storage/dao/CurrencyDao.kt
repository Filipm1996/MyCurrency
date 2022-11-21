package com.example.mycurrency.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mycurrency.data.storage.entities.Currency

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: Currency)

    @Query("SELECT * FROM currencyTable")
    suspend fun getAllCurrencies(): List<Currency>

    @Query("DELETE FROM currencyTable WHERE name= :name")
    fun deleteCurrencyByName (name : String)

    @Query("DELETE FROM currencyTable")
    fun deleteAll()

    @Query("UPDATE currencyTable SET rate = :rate AND addDate = :addDate WHERE name = :name")
    suspend fun updateRate(rate:String, addDate : String, name: String)
}