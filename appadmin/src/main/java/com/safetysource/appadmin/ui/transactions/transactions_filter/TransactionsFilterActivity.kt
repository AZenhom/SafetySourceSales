package com.safetysource.appadmin.ui.transactions.transactions_filter

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityTransactionsFilterBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.sheets.SelectListSheet
import com.safetysource.core.utils.getDateText
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.TransactionFilterModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class TransactionsFilterActivity :
    BaseActivity<ActivityTransactionsFilterBinding, TransactionsFilterViewModel>() {

    companion object {
        const val TRANSACTION_FILTER_MODEL = "TRANSACTION_FILTER_MODEL"
        fun getIntent(
            context: Context,
            transactionFilterModel: TransactionFilterModel?,
        ) =
            Intent(context, TransactionsFilterActivity::class.java).apply {
                putExtra(TRANSACTION_FILTER_MODEL, transactionFilterModel)
            }
    }

    override val viewModel: TransactionsFilterViewModel by viewModels()
    override val binding by viewBinding(ActivityTransactionsFilterBinding::inflate)

    private var categoriesList: List<ProductCategoryModel> = emptyList()
    private var productsList: List<ProductModel> = emptyList()

    override fun onActivityCreated() {
        initViews()
        initObservers()
        viewModel.getInitialData()
    }

    private fun initViews() {
        val anyText = getString(R.string.any)
        with(binding) {
            // TextViews text initialization
            viewModel.transactionFilterModel.let {
                tvCategory.text = it?.category?.name ?: anyText
                tvProduct.text = it?.product?.name ?: anyText
                tvDateFrom.text = it?.dateFrom?.time?.getDateText("EE, d MMM yyyy")
                tvDateTo.text = it?.dateTo?.time?.getDateText("EE, d MMM yyyy")
            }

            // Click Listeners
            toolbar.setNavigationOnClickListener { onBackPressed() }

            clCategory.setOnClickListener {
                SelectListSheet(
                    itemsList = categoriesList.toMutableList(),
                    anyItemObjectIfApplicable = ProductCategoryModel("-1", anyText),
                    selectedItem = viewModel.transactionFilterModel?.category,
                    sheetTitle = getString(R.string.product_categories),
                    sheetSubTitle = getString(R.string.please_pick_product_category)
                ) {
                    tvCategory.text = it?.name ?: anyText
                    tvProduct.text = anyText
                    viewModel.setCategory(it)
                }.show(supportFragmentManager, "CategorySheet")
            }

            clProduct.setOnClickListener {
                SelectListSheet(
                    itemsList = productsList.toMutableList(),
                    anyItemObjectIfApplicable = ProductModel("-1", anyText),
                    selectedItem = viewModel.transactionFilterModel?.product,
                    sheetTitle = getString(R.string.products),
                    sheetSubTitle = getString(R.string.please_pick_product)
                ) {
                    tvProduct.text = it?.name ?: anyText
                    viewModel.setProduct(it)
                }.show(supportFragmentManager, "ProductsSheet")
            }

            clDateFrom.setOnClickListener {
                with(Calendar.getInstance()) {
                    viewModel.getDateFrom()?.let { time = it }
                    DatePickerDialog(
                        this@TransactionsFilterActivity,
                        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                            set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                            tvDateFrom.text = time.time.getDateText("EE, d MMM yyyy")
                            viewModel.setDateFrom(time)
                        }, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }

            clDateTo.setOnClickListener {
                with(Calendar.getInstance()) {
                    viewModel.getDateTo()?.let { time = it }
                    DatePickerDialog(
                        this@TransactionsFilterActivity,
                        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                            set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                            tvDateTo.text = time.time.getDateText("EE, d MMM yyyy")
                            viewModel.setDateTo(time)
                        }, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            categoriesLiveData.observe(this@TransactionsFilterActivity) { categoriesList = it }
            productsLiveData.observe(this@TransactionsFilterActivity) { productsList = it }
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK, viewModel.getFilterResult())
        super.onBackPressed()
    }
}