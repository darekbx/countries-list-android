package com.darekbx.countrieslist.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.darekbx.countrieslist.BuildConfig
import com.darekbx.countrieslist.R
import com.darekbx.countrieslist.model.CountriesWrapper
import com.darekbx.countrieslist.utils.hide
import com.darekbx.countrieslist.utils.show
import com.darekbx.countrieslist.viewmodel.CountriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_countries.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_loading.*
import java.lang.IllegalStateException

@AndroidEntryPoint
class CountriesFragment : Fragment(R.layout.fragment_countries) {

    companion object {
        val ACTION_REFRESH_NOW = "action_refresh_now"
    }

    private val countriesViewModel: CountriesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareCountriesList()
        registerViewModel()
        loadCountries()

        refresh_button.setOnClickListener { loadCountries() }
    }

    override fun onStart() {
        super.onStart()
        context?.registerReceiver(refreshBroadcast, IntentFilter(ACTION_REFRESH_NOW))
    }

    override fun onStop() {
        super.onStop()
        try {
            context?.unregisterReceiver(refreshBroadcast)
        } catch (e: IllegalStateException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    private fun prepareCountriesList() {
        countries_list.adapter = countryAdapter
        countries_list.layoutManager = LinearLayoutManager(context)
    }

    private fun registerViewModel() {
        with(countriesViewModel) {
            countriesWrapper.observe(viewLifecycleOwner, { countriesWrapper ->
                handleCountriesResult(countriesWrapper)
                hideLoadingView()
            })
        }
    }

    private fun loadCountries(forceReload: Boolean = false) {
        error_container.hide()
        countriesViewModel.fetchCountries(forceReload)
        showLoadingView()
    }

    private fun handleCountriesResult(countriesWrapper: CountriesWrapper) {
        when (countriesWrapper.errorCode != null) {
            true -> handleError(countriesWrapper.errorCode)
            else -> countryAdapter.items = countriesWrapper.countries
        }
    }

    private fun handleError(errorCode: CountriesWrapper.ErrorCode) {
        val messageId = when (errorCode) {
            CountriesWrapper.ErrorCode.UNKNOWN_ERROR -> R.string.loading_error_unknown
            CountriesWrapper.ErrorCode.NO_INTERNET -> R.string.loading_error_no_internet
        }
        showErrorView(messageId)
    }

    private fun showErrorView(@StringRes messageId: Int) {
        error_message.setText(messageId)
        error_container.show()
    }

    private fun showLoadingView() {
        loading_container.show()
    }

    private fun hideLoadingView() {
        loading_container.hide()
    }

    private val refreshBroadcast = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.takeIf { it.action == ACTION_REFRESH_NOW }?.let {
                loadCountries(forceReload = true)
            }
        }
    }

    private val countryAdapter by lazy { CountryAdapter(context) }
}
