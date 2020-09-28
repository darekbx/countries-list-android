package com.darekbx.countrieslist.repository.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country")
class CountryDto (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "currencies") val currencies: String,
    @ColumnInfo(name = "calling_codes") val callingCodes: String,
    @ColumnInfo(name = "top_level_domain") val topLevelDomain: String
)
