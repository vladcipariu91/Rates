package com.vlad.android.currency.exchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vlad.android.currency.exchange.model.CurrenciesUseCase
import com.vlad.android.currency.exchange.model.CurrencyRate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class CurrencyViewModel @Inject constructor(private val currenciesUseCase: CurrenciesUseCase) :
    ViewModel() {

    private val currenciesLiveData = MutableLiveData<CurrencyFragmentState>()
    private val state = CurrencyFragmentState(isLoading = true)
    private var disposable: Disposable? = null

    init {
        currenciesLiveData.postValue(state)
        loadCurrencies("EUR")
    }

    private fun loadCurrencies(currencyCode: String) {
        disposable = currenciesUseCase.getCurrencies(currencyCode)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                currenciesLiveData.postValue(
                    state.copy(isLoading = false, currencies = it, isError = false)
                )
            }
            .doOnError {
                currenciesLiveData.postValue(state.copy(isLoading = false, isError = true))
            }
            .subscribe()
    }

    fun watchCurrencies(): LiveData<CurrencyFragmentState> {
        return currenciesLiveData
    }

    fun onCurrencySelected(index: Int, currency: Currency) {
        currenciesUseCase.swapFirstWith(index)
            .doOnNext {
                currenciesLiveData.postValue(
                    state.copy(isLoading = false, currencies = it, updateIndex = 0, isError = false)
                )
            }
            .subscribe()

        dispose()
        loadCurrencies(currency.currencyCode)
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }

    private fun dispose() {
        disposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    fun updateValues(newValue: Double) {
        currenciesUseCase.updateValues(newValue)
            .doOnNext {
                currenciesLiveData.postValue(
                    state.copy(isLoading = false, currencies = it, isError = false)
                )
            }
            .subscribe()
    }
}

data class CurrencyFragmentState(
    val isLoading: Boolean = false,
    val currencies: List<CurrencyRate> = emptyList(),
    val updateIndex: Int = 1,
    val isError: Boolean = false
)