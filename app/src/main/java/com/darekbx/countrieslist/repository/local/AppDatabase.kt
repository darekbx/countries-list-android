package com.darekbx.countrieslist.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.countrieslist.repository.local.entities.CountryDto

@Database(entities = arrayOf(CountryDto::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        val DB_NAME = "app_database"
    }

    abstract fun countriesDao(): CountriesDao
}
