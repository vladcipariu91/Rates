package com.vlad.android.currency.exchange

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vlad.android.currency.R
import com.vlad.android.currency.databinding.FragmentCurrencyBinding
import com.vlad.android.currency.exchange.model.CurrencyRate
import com.vlad.android.currency.exchange.model.getFlagUrl
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject

class CurrencyFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<CurrencyViewModel> { viewModelFactory }

    private lateinit var binding: FragmentCurrencyBinding

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        viewModel.watchCurrencies().observe(viewLifecycleOwner, Observer {
            when {
                it.isLoading -> {
                    binding.loadingView.visibility = View.VISIBLE
                    binding.errorView.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                it.isError -> {
                    binding.loadingView.visibility = View.GONE
                    binding.errorView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                it.currencies.isEmpty() -> {
                    binding.loadingView.visibility = View.GONE
                    binding.errorView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
                it.currencies.isNotEmpty() -> {
                    binding.loadingView.visibility = View.GONE
                    binding.errorView.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                    val recyclerView = binding.recyclerView
                    recyclerView.visibility = View.VISIBLE

                    if (recyclerView.adapter == null) {
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        recyclerView.adapter =
                            CurrencyAdapter(
                                it.currencies.toMutableList(),
                                this,
                                onItemClicked = { index, currencyItem ->
                                    viewModel.onCurrencySelected(index, currencyItem)
                                },
                                onValueChanged = { newValue -> viewModel.updateValues(newValue) })
                    } else {
                        (recyclerView.adapter as CurrencyAdapter).update(
                            it.currencies,
                            it.updateIndex
                        )
                    }
                }
            }
        })
    }

    private fun setupActionBar() {
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.toolbar)
    }
}

class CurrencyAdapter(
    private val currencies: MutableList<CurrencyRate>,
    private val lifecycle: Fragment,
    private val onItemClicked: (Int, Currency) -> Unit,
    private val onValueChanged: (Double) -> Unit
) : RecyclerView.Adapter<CurrencyViewHolder>() {

    private var position: Int = -1

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view, onValueChanged)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val item = currencies[position]
        holder.codeTextView.text = item.currency.currencyCode
        holder.nameTextView.text = item.currency.displayName
        holder.rateEditText.setText(String.format("%.2f", item.value))

        Glide
            .with(lifecycle)
            .load(item.currency.getFlagUrl())
            .placeholder(R.drawable.flag_placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.flagImageView)

        holder.itemView.setOnClickListener {
            this.position = position
            onItemClicked(position, item.currency)
        }
        holder.rateEditText.isEnabled = position == 0
    }

    override fun getItemCount() = currencies.size

    fun update(newCurrencies: List<CurrencyRate>, updateIndex: Int) {
        this.currencies.clear()
        this.currencies.addAll(newCurrencies)

        if (updateIndex == 0) {
            if (position != -1) {
                notifyItemMoved(0, position)
                notifyItemMoved(position, 0)
            }
        }
        for (i in updateIndex until currencies.size) {
            notifyItemChanged(i, Unit)
        }
    }

    override fun getItemId(position: Int): Long {
        return currencies[position].currency.numericCode.toLong()
    }
}

class CurrencyViewHolder(
    view: View,
    private val onValueChanged: (Double) -> Unit
) : RecyclerView.ViewHolder(view) {
    val codeTextView: TextView = view.findViewById(R.id.currencyCodeTextView)
    val nameTextView: TextView = view.findViewById(R.id.currencyNameTextView)
    val rateEditText: EditText = view.findViewById(R.id.rateEditText)
    val flagImageView: ImageView = view.findViewById(R.id.flagImageView)

    init {
        rateEditText.doAfterTextChanged {
            if (adapterPosition == 0 && it != null && it.isNotEmpty()) {
                onValueChanged(it.toString().toDouble())
            }
        }
    }
}