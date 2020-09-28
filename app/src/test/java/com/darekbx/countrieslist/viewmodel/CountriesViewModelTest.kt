package com.darekbx.countrieslist.viewmodel

import androidx.test.core.app.ApplicationProvider
import com.darekbx.countrieslist.model.CountriesWrapper
import com.darekbx.countrieslist.model.remote.Country
import com.darekbx.countrieslist.model.remote.Currency
import com.darekbx.countrieslist.repository.local.CountriesDao
import com.darekbx.countrieslist.repository.local.entities.CountryDto
import com.darekbx.countrieslist.repository.remote.CountriesService
import com.darekbx.countrieslist.utils.AppPreferences
import com.darekbx.countrieslist.utils.NetworkUtils
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Call
import retrofit2.Response
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.util.*

@RunWith(RobolectricTestRunner::class)
class CountriesViewModelTest {

    @Test
    fun `fetchCountries - should reload, no internet`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()
        val countriesDao = mock<CountriesDao>()
        val networkUtils = mockDisconnectedNetworkUtil()

        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext()).also {
            saveLastReloadedYesterday(it)
        }

        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))

        countriesViewModel.ioScope = CoroutineScope(Dispatchers.Main)

        // When
        countriesViewModel.fetchCountries()

        // Then
        countriesViewModel.countriesWrapper.value?.let {
            assertEquals(CountriesWrapper.ErrorCode.NO_INTERNET, it.errorCode)
            assertEquals(0, it.countries?.size)
        }
    }

    @Test
    fun `fetchCountries - load from cache`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()
        val networkUtils = mockConnectedNetworkUtil()
        val countriesDao = mock<CountriesDao>().apply {
            doReturn(1).whenever(this).countCountries()
        }

        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext()).also {
            saveLastReloadedToday(it)
        }

        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))

        countriesViewModel.ioScope = CoroutineScope(Dispatchers.Main)

        // When
        countriesViewModel.fetchCountries()

        // Then
        assertNull(countriesViewModel.countriesWrapper.value?.errorCode)
        verify(countriesDao, times(1)).countCountries()
        verify(countriesDao, times(1)).getAllCountries()
    }

    @Test
    fun `fetchCountries - load from api`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()

        val countriesApiCall = mock<Call<List<Country>>>()
        val response = Response.success(listOf(createCountry()))
        doReturn(response).whenever(countriesApiCall).execute()
        doReturn(countriesApiCall).whenever(countriesService).requestAllCountries()

        val networkUtils = mockConnectedNetworkUtil()
        val countriesDao = mock<CountriesDao>()
        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext())

        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))

        countriesViewModel.ioScope = CoroutineScope(Dispatchers.Main)

        // When
        countriesViewModel.fetchCountries()

        // Then
        verify(countriesDao, times(1)).deleteAllCountries()

        val countriesCaptor = argumentCaptor<CountryDto>()
        verify(countriesDao, times(1)).addCountries(countriesCaptor.capture())

        with(countriesCaptor.firstValue) {
            assertEquals("Poland", name)
            assertEquals("PLN (zł)", currencies)
            assertEquals("48", callingCodes)
            assertEquals(".pl", topLevelDomain)
        }

        countriesViewModel.countriesWrapper.value?.let {
            assertNull(it.errorCode)
            assertEquals(1, it.countries?.size)

            with(it.countries.first()) {
                assertEquals("Poland", name)
                assertEquals("PLN (zł)", currencies)
                assertEquals("48", callingCodes)
                assertEquals(".pl", topLevelDomain)
            }
        }
    }

    @Test
    fun `fetchCountries - load from api, force refresh`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()

        val countriesApiCall = mock<Call<List<Country>>>()
        val response = Response.success(listOf(createCountry()))
        doReturn(response).whenever(countriesApiCall).execute()
        doReturn(countriesApiCall).whenever(countriesService).requestAllCountries()

        val networkUtils = mockConnectedNetworkUtil()
        val countriesDao = mock<CountriesDao>().apply {
            doReturn(1).whenever(this).countCountries()
        }
        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext()).also {
            saveLastReloadedToday(it)
        }

        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))

        countriesViewModel.ioScope = CoroutineScope(Dispatchers.Main)

        // When
        countriesViewModel.fetchCountries(forceRefresh = true)

        // Then
        verify(countriesDao, times(1)).deleteAllCountries()

        val countriesCaptor = argumentCaptor<CountryDto>()
        verify(countriesDao, times(1)).addCountries(countriesCaptor.capture())

        with(countriesCaptor.firstValue) {
            assertEquals("Poland", name)
            assertEquals("PLN (zł)", currencies)
            assertEquals("48", callingCodes)
            assertEquals(".pl", topLevelDomain)
        }

        countriesViewModel.countriesWrapper.value?.let {
            assertNull(it.errorCode)
            assertEquals(1, it.countries?.size)

            with(it.countries.first()) {
                assertEquals("Poland", name)
                assertEquals("PLN (zł)", currencies)
                assertEquals("48", callingCodes)
                assertEquals(".pl", topLevelDomain)
            }
        }
    }

    @Test
    fun `fetchCountries - unknown error, request exception`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()
        doThrow(RuntimeException()).whenever(countriesService).requestAllCountries()

        val networkUtils = mockConnectedNetworkUtil()
        val countriesDao = mock<CountriesDao>()
        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext())
        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))

        countriesViewModel.ioScope = CoroutineScope(Dispatchers.Main)

        // When
        countriesViewModel.fetchCountries()

        // Then
        countriesViewModel.countriesWrapper.value?.let {
            assertEquals(CountriesWrapper.ErrorCode.UNKNOWN_ERROR, it.errorCode)
            assertEquals(0, it.countries?.size)
        }
    }

    @Test
    fun `fetchCountries - unknown error, not successfull`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()

        val countriesApiCall = mock<Call<List<Country>>>()
        val responseBody = ResponseBody.create(MediaType.parse("text"), "")
        val response = Response.error<List<Country>>(HttpURLConnection.HTTP_INTERNAL_ERROR, responseBody)
        doReturn(response).whenever(countriesApiCall).execute()
        doReturn(countriesApiCall).whenever(countriesService).requestAllCountries()

        val networkUtils = mockConnectedNetworkUtil()
        val countriesDao = mock<CountriesDao>()
        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext())
        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))

        countriesViewModel.ioScope = CoroutineScope(Dispatchers.Main)

        // When
        countriesViewModel.fetchCountries()

        // Then
        countriesViewModel.countriesWrapper.value?.let {
            assertEquals(CountriesWrapper.ErrorCode.UNKNOWN_ERROR, it.errorCode)
            assertEquals(0, it.countries?.size)
        }
    }

    @Test
    fun `wasReloadedToday - false`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()
        val countriesDao = mock<CountriesDao>()
        val networkUtils = mock<NetworkUtils>()
        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext())
        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))
        saveLastReloadedYesterday(appPreferences)

        // When
        val result = countriesViewModel.wasReloadedToday()

        // Then
        assertFalse(result)
    }

    @Test
    fun `wasReloadedToday - true`() {
        // Given
        val countriesService = mock<CountriesService.CountriesProtocol>()
        val countriesDao = mock<CountriesDao>()
        val networkUtils = mock<NetworkUtils>()
        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext())
        val countriesViewModel =
            spy(CountriesViewModel(countriesService, countriesDao, networkUtils, appPreferences))

        appPreferences.lastReloadDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        // When
        val result = countriesViewModel.wasReloadedToday()

        // Then
        assertTrue(result)
    }

    private fun createCountry() =
        Country("Poland", listOf(Currency("PLN", "zł")), listOf("48"), listOf(".pl"))

    private fun mockDisconnectedNetworkUtil() =
        mock<NetworkUtils>().apply {
            doReturn(false).whenever(this).isConnected()
        }

    private fun mockConnectedNetworkUtil() =
        mock<NetworkUtils>().apply {
            doReturn(true).whenever(this).isConnected()
        }

    private fun saveLastReloadedYesterday(appPreferences: AppPreferences) {
        appPreferences.lastReloadDay = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.get(Calendar.DAY_OF_YEAR)
    }

    private fun saveLastReloadedToday(appPreferences: AppPreferences) {
        appPreferences.lastReloadDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }
}
