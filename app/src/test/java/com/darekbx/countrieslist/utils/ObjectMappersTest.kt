package com.darekbx.countrieslist.utils

import com.darekbx.countrieslist.model.Country
import com.darekbx.countrieslist.model.remote.Currency
import com.darekbx.countrieslist.model.remote.Country as RemoteCountry
import com.darekbx.countrieslist.repository.local.entities.CountryDto

import org.junit.Test

import org.junit.Assert.*

class ObjectMappersTest {

    @Test
    fun remoteCountryToCountry() {
        // Given
        val remoteCountry = RemoteCountry("name", listOf(Currency("PLN", "zł")), listOf("48", "45"), listOf(".pl"))

        // When
        var country = ObjectMappers.remoteCountryToCountry(remoteCountry)

        // Then
        with(country) {
            assertEquals("name", name)
            assertEquals("PLN (zł)", currencies)
            assertEquals("48, 45", callingCodes)
            assertEquals(".pl", topLevelDomain)
        }
    }

    @Test
    fun countryToCountryDto() {
        // Given
        val country = Country("name", "PLN", "48", ".pl")

        // When
        val countryDto = ObjectMappers.countryToCountryDto(country)

        // Then
        with(countryDto) {
            assertNull(id)
            assertEquals("name", name)
            assertEquals("PLN", currencies)
            assertEquals("48", callingCodes)
            assertEquals(".pl", topLevelDomain)
        }
    }

    @Test
    fun countryDtoToCountry() {
        // Given
        val countryDto = CountryDto(1, "name", "PLN", "48", ".pl")

        // When
        val country = ObjectMappers.countryDtoToCountry(countryDto)

        // Then
        with(country) {
            assertEquals("name", name)
            assertEquals("PLN", currencies)
            assertEquals("48", callingCodes)
            assertEquals(".pl", topLevelDomain)
        }
    }
}
