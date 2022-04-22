package com.safetysource.appadmin.ui.transactions.transactions_filter

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.TransactionFilterModel
import com.safetysource.data.model.response.StatefulResult
import com.safetysource.data.repository.ProductCategoryRepository
import com.safetysource.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionsFilterViewModel @Inject constructor(
    private val productCategoryRepository: ProductCategoryRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    var transactionFilterModel: TransactionFilterModel? =
        savedStateHandle[TransactionsFilterActivity.TRANSACTION_FILTER_MODEL]

    private val _categoriesLiveData = LiveEvent<List<ProductCategoryModel>>()
    val categoriesLiveData: LiveData<List<ProductCategoryModel>> get() = _categoriesLiveData

    private val _productsLiveData = LiveEvent<List<ProductModel>>()
    val productsLiveData: LiveData<List<ProductModel>> get() = _productsLiveData


    fun getInitialData() = safeLauncher {
        showLoading()
        getProductCategories()
        getProducts()
        hideLoading()
    }

    fun getFilterResult(): Intent = Intent().apply {
        putExtra(TransactionsFilterActivity.TRANSACTION_FILTER_MODEL, transactionFilterModel)
    }

    fun setCategory(category: ProductCategoryModel?) {
        transactionFilterModel?.category = category
        transactionFilterModel?.product = null
        _productsLiveData.value = emptyList()

        safeLauncher {
            showLoading()
            getProducts()
            hideLoading()
        }
    }

    fun setProduct(product: ProductModel?) {
        transactionFilterModel?.product = product
    }

    fun getDateFrom(): Date? = transactionFilterModel?.dateFrom

    fun setDateFrom(dateFrom: Date) {
        transactionFilterModel?.dateFrom = dateFrom
    }

    fun getDateTo(): Date? = transactionFilterModel?.dateTo

    fun setDateTo(dateTo: Date) {
        transactionFilterModel?.dateTo = dateTo
    }

    private suspend fun getProductCategories() {
        val result = productCategoryRepository.getProductCategories()
        if (result is StatefulResult.Success)
            _categoriesLiveData.value = result.data ?: listOf()
        else
            handleError(result.errorModel)
    }

    private suspend fun getProducts() {
        transactionFilterModel?.category?.let {
            val result = productRepository.getCategoryProducts(it.id ?: return)
            if (result is StatefulResult.Success)
                _productsLiveData.value = result.data ?: listOf()
            else
                handleError(result.errorModel)
        }
    }
}