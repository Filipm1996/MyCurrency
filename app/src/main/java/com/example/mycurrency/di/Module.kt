package com.example.mycurrency.di

import android.content.Context
import androidx.room.Room
import com.example.currencies.data.Retrofit.CoinGecko.CoinGeckoAPI
import com.example.currencies.data.Retrofit.CoinGecko.RetrofitInstanceForCoinGecko
import com.example.currencies.data.Retrofit.NBP.NBPapi
import com.example.currencies.data.Retrofit.NBP.RetrofitInstanceForNBP
import com.example.currencies.data.db.CurrencyDataBase
import com.example.mycurrency.data.network.NetworkRepository
import com.example.mycurrency.data.network.NetworkRepositoryImpl
import com.example.mycurrency.data.storage.CurrencyDbRepository
import com.example.mycurrency.data.storage.CurrencyDbRepositoryImpl
import com.example.mycurrency.data.storage.dao.CurrencyDao
import com.example.mycurrency.other.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideNbpRetrofitInstance(): RetrofitInstanceForNBP = RetrofitInstanceForNBP

    @Singleton
    @Provides
    fun provideNbpDao(): NBPapi = RetrofitInstanceForNBP.api

    @Singleton
    @Provides
    fun provideCoinGeckoRetrofitInstance(): RetrofitInstanceForCoinGecko =
        RetrofitInstanceForCoinGecko

    @Singleton
    @Provides
    fun provideCoinGeckoDao(): CoinGeckoAPI = RetrofitInstanceForCoinGecko.api

    @Singleton
    @Provides
    fun provideNetworkRepository(
        nbPapi: NBPapi,
        coinGeckoAPI: CoinGeckoAPI
    ): NetworkRepository = NetworkRepositoryImpl(coinGeckoAPI, nbPapi)

    @Singleton
    @Provides
    fun provideCurrencyDateBase(
        @ApplicationContext context: Context
    ): CurrencyDataBase =
        Room.databaseBuilder(context, CurrencyDataBase::class.java, Constants.MY_LIST_DATABASE)
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideCurrencyDao(
        currencyDataBase: CurrencyDataBase
    ) : CurrencyDao = currencyDataBase.currencyDao()

    @Singleton
    @Provides
    fun provideCurrencyDbRepository (
        currencyDao: CurrencyDao
    ) : CurrencyDbRepository = CurrencyDbRepositoryImpl(currencyDao)
}