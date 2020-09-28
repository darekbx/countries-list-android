package com.darekbx.countrieslist.viewmodel

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.darekbx.countrieslist.model.CountriesWrapper
import com.darekbx.countrieslist.model.Country
import com.darekbx.countrieslist.repository.local.CountriesDao
import com.darekbx.countrieslist.repository.remote.CountriesService
import com.darekbx.countrieslist.utils.AppPreferences
import com.darekbx.countrieslist.utils.NetworkUtils
import com.darekbx.countrieslist.utils.ObjectMappers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.*

open class CountriesViewModel @ViewModelInject constructor(
    private val countriesService: CountriesService.CountriesProtocol,
    private val countriesDao: CountriesDao,
    private val networkUtils: NetworkUtils,
    private val appPreferences: AppPreferences
): BaseViewModel() {

    companion object {
        val LOG_TAG = "CountriesViewModel"
    }

    val countriesWrapper = MutableLiveData<CountriesWrapper>()

    fun fetchCountries(forceRefresh: Boolean = false) {
        ioScope.launch {
            try {
                val shouldReload = forceRefresh || shouldReloadCache()

                if (shouldReload && !networkUtils.isConnected()) {
                    countriesWrapper.postValue(CountriesWrapper(errorCode = CountriesWrapper.ErrorCode.NO_INTERNET))
                    return@launch
                }

                val countriesList = when {
                    shouldReload -> {
                        loadCountriesFromApi().apply {
                            cacheResults(this)
                        }
                    }
                    else -> loadCountriesFromDatabase()
                }

                countriesWrapper.postValue(CountriesWrapper(countriesList))
            } catch (e: Exception) {
                countriesWrapper.postValue(CountriesWrapper(errorCode = CountriesWrapper.ErrorCode.UNKNOWN_ERROR))
            }
        }
    }

    private fun loadCountriesFromApi(): List<Country> {
        Log.v(LOG_TAG, "Load countries from Api")
        val response = countriesService.requestAllCountries().execute()
        when {
            response.isSuccessful -> {
                response.body()?.let { remoteCountries ->
                    return remoteCountries.map { remoteCountry ->
                        ObjectMappers.remoteCountryToCountry(remoteCountry)
                    }
                } ?: throw IllegalStateException("Response body is null")
            }
            else -> throw HttpException(response)
        }
    }

    private fun loadCountriesFromDatabase(): List<Country> {
        Log.v(LOG_TAG, "Load countries from Database")
        val countryDtos = countriesDao.getAllCountries()
        return countryDtos.map { country -> ObjectMappers.countryDtoToCountry(country) }
    }

    private fun shouldReloadCache(): Boolean {
        val countriesCount = countriesDao.countCountries()
        return when  {
            !wasReloadedToday() -> true
            countriesCount == 0 -> true
            else -> false
        }
    }

    private fun cacheResults(countries: List<Country>) {
        Log.v(LOG_TAG, "Cache countries")
        val countryDtos = countries
            .map { country -> ObjectMappers.countryToCountryDto(country) }
            .toTypedArray()
        with(countriesDao) {
            deleteAllCountries()
            addCountries(*countryDtos)
        }
        saveLastReloadCacheDate()
    }

    private fun saveLastReloadCacheDate() {
        appPreferences.lastReloadDay = getCurrentDay()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    open fun wasReloadedToday(): Boolean {
        val lastReloadDay = appPreferences.lastReloadDay
        val currentDay = getCurrentDay()
        return currentDay == lastReloadDay
    }

    private fun getCurrentDay() = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
}
