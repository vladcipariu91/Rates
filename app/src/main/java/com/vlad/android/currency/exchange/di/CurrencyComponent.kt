package com.vlad.android.currency.exchange.di

import com.vlad.android.currency.exchange.CurrencyFragment
import dagger.Subcomponent

@Subcomponent(modules = [CurrencyModule::class])
interface CurrencyComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): CurrencyComponent
    }

    fun inject(fragment: CurrencyFragment)
}