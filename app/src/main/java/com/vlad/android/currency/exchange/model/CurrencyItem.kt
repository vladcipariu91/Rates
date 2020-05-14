package com.vlad.android.currency.exchange.model

import com.vlad.android.currency.network.CurrencyResponse
import java.util.*

fun CurrencyResponse.toItems(): List<CurrencyRate> {
    val items = mutableListOf<CurrencyRate>()
    items.add(CurrencyRate(Currency.getInstance(baseCurrency), 1.0))
    rates.forEach {
        items.add(CurrencyRate(Currency.getInstance(it.key), it.value))
    }

    return items
}

fun Currency.getFlagUrl(): String {
    return if (currencyCode == "EUR") {
        "https://cdn11.bigcommerce.com/s-2lbnjvmw4d/images/stencil/1280x1280/products/2899/3595/Europe300__65153.1567703960.gif?c=2"
    } else {
        "https://flagpedia.net/data/flags/normal/${currencyCode.take(2).toLowerCase(Locale.getDefault())}.png"
    }
}

data class CurrencyRate(val currency: Currency,
                        var rate: Double,
                        var value: Double = rate)