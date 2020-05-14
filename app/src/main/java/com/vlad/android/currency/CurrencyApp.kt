package com.vlad.android.currency

import com.vlad.android.currency.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


class CurrencyApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}