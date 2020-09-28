package com.darekbx.countrieslist.repository.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.darekbx.countrieslist.repository.local.entities.CountryDto
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDatabaseTest {

    private lateinit var countriesDao: CountriesDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room
            .databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .allowMainThreadQueries()
            .build()
        countriesDao = database.countriesDao()
    }

    @Test
    fun `test addCountries`() {
        // Given
        val country = CountryDto(null, "Name", "PLN (zł)", "48", ".pl")

        // When
        countriesDao.addCountries(country)

        // Then
        val countries = countriesDao.getAllCountries()
        assertEquals(1, countries.size)

        with(countries.first()) {
            assertEquals("Name", name)
            assertEquals("PLN (zł)", currencies)
            assertEquals("48", callingCodes)
            assertEquals(".pl", topLevelDomain)
        }
    }

    @Test
    fun `test deleteCountries`() {
        // Given
        val country = CountryDto(null, "Name", "PLN (zł)", "48", ".pl")
        countriesDao.addCountries(country)
        assert(1 == countriesDao.countCountries())

        // When
        countriesDao.deleteAllCountries()

        // Then
        assertEquals(0, countriesDao.countCountries())
    }
}
