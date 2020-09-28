package com.darekbx.countrieslist.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darekbx.countrieslist.databinding.AdapterCountryBinding
import com.darekbx.countrieslist.model.Country

class CountryAdapter(val context: Context?)
    : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    var items = listOf<Country>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = AdapterCountryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val part = items.get(position)
        viewHolder.bind(part)
    }

    val inflater by lazy { LayoutInflater.from(context) }

    class ViewHolder(val binding: AdapterCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            binding.country = country
            binding.executePendingBindings()
        }
    }
}
