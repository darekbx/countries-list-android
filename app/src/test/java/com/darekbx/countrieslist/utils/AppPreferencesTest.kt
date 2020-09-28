package com.darekbx.countrieslist.utils

import androidx.test.core.app.ApplicationProvider
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppPreferencesTest {

    @Test
    fun `Test last reload day get & set`() {
        // Given
        val appPreferences = AppPreferences(ApplicationProvider.getApplicationContext())

        // When
        appPreferences.lastReloadDay = 12

        // Then
        assertEquals(12, appPreferences.lastReloadDay)
    }
}
