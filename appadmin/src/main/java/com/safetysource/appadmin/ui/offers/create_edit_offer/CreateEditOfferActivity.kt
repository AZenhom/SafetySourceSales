package com.safetysource.appadmin.ui.offers.create_edit_offer

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityCreateEditOfferBinding
import com.safetysource.appadmin.ui.product_categories.create_edit_category.CreateEditProductCategoryActivity
import com.safetysource.core.base.BaseActivity
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.ProductModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEditOfferActivity :
    BaseActivity<ActivityCreateEditOfferBinding, CreateEditOfferViewModel>() {

    companion object {
        const val OFFER_TO_EDIT = "OFFER_TO_EDIT"
        fun getIntent(context: Context, offerToEdit: OfferModel? = null) =
            Intent(context, CreateEditProductCategoryActivity::class.java).apply {
                putExtra(OFFER_TO_EDIT, offerToEdit)
            }
    }

    override val viewModel: CreateEditOfferViewModel by viewModels()
    override val binding by viewBinding(ActivityCreateEditOfferBinding::inflate)

    private var categoriesList: List<ProductCategoryModel> = emptyList()
    private var productsList: List<ProductModel> = emptyList()

    override fun onActivityCreated() {
        initViews()
        initObservers()
        viewModel.getInitialData()
    }

    private fun initViews() {

    }

    private fun initObservers() {
        with(viewModel) {
            categoriesLiveData.observe(this@CreateEditOfferActivity) { categoriesList = it }
            productsLiveData.observe(this@CreateEditOfferActivity) { productsList = it }
        }
    }
}