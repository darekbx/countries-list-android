package com.darekbx.countrieslist.utils

import android.content.Context
import com.darekbx.countrieslist.CountriesApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferences @Inject constructor(@ApplicationContext val context: Context) {

    companion object {
        private val LAST_RELOAD_DAY_KEY = "last_reload_day_key"
    }

    var lastReloadDay: Int
        get() {
            return preferences.getInt(LAST_RELOAD_DAY_KEY, 0)
        }
        set(value) {
            preferences.edit().putInt(LAST_RELOAD_DAY_KEY, value).apply()
        }

    private val preferences by lazy {
        context.getSharedPreferences(
            CountriesApplication::class.java.simpleName, Context.MODE_PRIVATE
        )
    }
}
