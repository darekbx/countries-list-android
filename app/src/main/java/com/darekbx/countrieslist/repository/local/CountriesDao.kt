package com.darekbx.countrieslist.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.countrieslist.repository.local.entities.CountryDto

@Dao
interface CountriesDao {

    @Query("SELECT COUNT(id) FROM country")
    fun countCountries(): Int

    @Query("SELECT * FROM country ORDER BY name")
    fun getAllCountries(): List<CountryDto>

    @Query("DELETE FROM country")
    fun deleteAllCountries()

    @Insert
    fun addCountries(vararg countryDtos: CountryDto)
}
