package com.vlad.android.currency.exchange.di

import androidx.lifecycle.ViewModel
import com.vlad.android.currency.di.ViewModelFactoryModel
import com.vlad.android.currency.di.ViewModelKey
import com.vlad.android.currency.exchange.CurrencyFragment
import com.vlad.android.currency.exchange.CurrencyViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class CurrencyModule {

    @ContributesAndroidInjector(modules = [ViewModelFactoryModel::class])
    abstract fun bindCurrencyFragment(): CurrencyFragment

    @Binds
    @IntoMap
    @ViewModelKey(CurrencyViewModel::class)
    abstract fun bindCurrencyViewModel(viewModel: CurrencyViewModel): ViewModel
}