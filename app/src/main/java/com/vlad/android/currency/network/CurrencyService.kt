package com.vlad.android.currency.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface CurrencyService {

    @GET("api/android/latest")
    fun getCurrencies(@Query("base") currency: String): Observable<CurrencyResponse>
}