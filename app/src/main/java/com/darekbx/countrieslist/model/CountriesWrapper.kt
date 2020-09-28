package com.darekbx.countrieslist.model

class CountriesWrapper (
    val countries: List<Country> = emptyList(),
    val errorCode: ErrorCode? = null
) {

    enum class ErrorCode {
        UNKNOWN_ERROR,
        NO_INTERNET
    }
}
