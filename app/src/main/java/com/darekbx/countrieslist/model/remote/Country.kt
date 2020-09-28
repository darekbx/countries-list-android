package com.darekbx.countrieslist.model.remote

class Country(
    val name: String,
    val currencies: List<Currency>,
    val callingCodes: List<String>,
    val topLevelDomain: List<String>
)
