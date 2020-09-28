package com.darekbx.countrieslist.repository.remote

import com.darekbx.countrieslist.BuildConfig
import com.darekbx.countrieslist.model.remote.Country
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class CountriesService {

    companion object {
        val ALL_COUNTRIES_SERVICE_URL = "https://restcountries.eu"
    }

    interface CountriesProtocol {

        @GET("rest/v2/all")
        fun requestAllCountries(): Call<List<Country>>
    }

    fun create() =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ALL_COUNTRIES_SERVICE_URL)
            .client(okHttpBuilder().build())
            .build()
            .create(CountriesProtocol::class.java)

    private fun okHttpBuilder() =
        OkHttpClient.Builder().apply {
            addHttpLogging(this)
        }

    private fun addHttpLogging(httpClient: OkHttpClient.Builder) {
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            httpClient.addInterceptor(loggingInterceptor)
        }
    }
}
