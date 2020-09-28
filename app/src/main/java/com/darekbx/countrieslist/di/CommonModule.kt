package com.darekbx.countrieslist.di

import android.content.Context
import androidx.room.Room
import com.darekbx.countrieslist.repository.local.AppDatabase
import com.darekbx.countrieslist.repository.remote.CountriesService
import com.darekbx.countrieslist.utils.NetworkUtils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object CommonModule {

    @Provides
    fun countriesService()
        = CountriesService().create()

    @Singleton
    @Provides
    fun appDatabase(@ApplicationContext context: Context) =
        Room
            .databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()

    @Singleton
    @Provides
    fun provideCountriesDao(db: AppDatabase) = db.countriesDao()

    @Singleton
    @Provides
    fun provietNetworkUtils(@ApplicationContext context: Context) =
        NetworkUtils(context)
}
