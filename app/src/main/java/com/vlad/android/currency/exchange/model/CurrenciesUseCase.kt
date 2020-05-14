package com.vlad.android.currency.exchange.model

import com.vlad.android.currency.network.CurrencyService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrenciesUseCase @Inject constructor(private val currencyService: CurrencyService) {

    private lateinit var currencyRates: List<CurrencyRate>
    private var multiplier = 1.0

    fun getCurrencies(currencyCode: String): Observable<List<CurrencyRate>> {
        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.single())
            .flatMap {
                currencyService.getCurrencies(currencyCode).map { response ->
                    val new = response.toItems()

                    if (!::currencyRates.isInitialized) {
                        currencyRates = new
                    } else {
                        currencyRates.forEach { currencyRate ->
                            new.find { it.currency == currencyRate.currency }?.let {
                                currencyRate.value = it.rate * multiplier
                            }
                        }
                    }

                    currencyRates
                }
            }
    }

    fun swapFirstWith(index: Int): Observable<List<CurrencyRate>> {
        val new = currencyRates.toList().toMutableList()
        new.swap(0, index)
        currencyRates = new
        multiplier = currencyRates[0].value
        val oldRate = currencyRates[0].rate
        currencyRates[0].rate = 1.0
        currencyRates.drop(1).forEach {
            it.rate = it.rate / oldRate
            it.value = it.rate * multiplier
        }

        return Observable.just(currencyRates)
    }

    private fun MutableList<CurrencyRate>.swap(index1: Int, index2: Int) {
        val tmp = this[index1]
        this[index1] = this[index2]
        this[index2] = tmp
    }

    fun updateValues(newValue: Double): Observable<List<CurrencyRate>> {
        multiplier = newValue
        currencyRates[0].value = multiplier
        currencyRates.drop(1).forEach { it.value = it.rate * multiplier }

        return Observable.just(currencyRates)
    }
}