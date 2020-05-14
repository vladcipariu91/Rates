package com.vlad.android.currency.network

import com.google.gson.annotations.SerializedName

class CurrencyResponse(
    @SerializedName("baseCurrency") val baseCurrency: String,
    @SerializedName("rates") val rates: Map<String, Double>
)