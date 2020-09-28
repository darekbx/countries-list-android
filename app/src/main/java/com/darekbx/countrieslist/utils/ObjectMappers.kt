package com.darekbx.countrieslist.utils

import com.darekbx.countrieslist.model.Country
import com.darekbx.countrieslist.model.remote.Country as RemoteCountry
import com.darekbx.countrieslist.repository.local.entities.CountryDto

object ObjectMappers {

    fun remoteCountryToCountry(remoteCountry: RemoteCountry) =
        Country(
            remoteCountry.name,
            remoteCountry.currencies.joinToString(", ") { "${it.code} (${it.symbol})" },
            remoteCountry.callingCodes.joinToString(", "),
            remoteCountry.topLevelDomain.joinToString(", ")
        )

    fun countryToCountryDto(country: Country) =
        CountryDto(
            null,
            country.name,
            country.currencies,
            country.callingCodes,
            country.topLevelDomain
        )

    fun countryDtoToCountry(countryDto: CountryDto) =
        Country(
            countryDto.name,
            countryDto.currencies,
            countryDto.callingCodes,
            countryDto.topLevelDomain
        )
}
