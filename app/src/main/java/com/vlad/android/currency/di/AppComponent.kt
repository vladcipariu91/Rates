package com.vlad.android.currency.di

import android.content.Context
import com.vlad.android.currency.CurrencyApp
import com.vlad.android.currency.exchange.di.CurrencyModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        AndroidSupportInjectionModule::class,
        CurrencyModule::class
    ]
)
interface AppComponent : AndroidInjector<CurrencyApp> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}