package com.safetysource.appadmin.ui.products.create_edit_product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.safetysource.appadmin.databinding.ActivityCreateEditProductBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.utils.convertArabicNumbersIfExist
import com.safetysource.core.utils.getDigit
import com.safetysource.data.model.ProductModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEditProductActivity :
    BaseActivity<ActivityCreateEditProductBinding, CreateEditProductViewModel>() {

    companion object {
        const val PRODUCT_CATEGORY_ID = "PRODUCT_CATEGORY_ID"
        const val PRODUCT_TO_EDIT = "PRODUCT_TO_EDIT"
        fun getIntent(
            context: Context,
            productCategoryId: String,
            productToEdit: ProductModel? = null
        ) =
            Intent(context, CreateEditProductActivity::class.java).apply {
                putExtra(PRODUCT_CATEGORY_ID, productCategoryId)
                putExtra(PRODUCT_TO_EDIT, productToEdit)
            }
    }

    override val viewModel: CreateEditProductViewModel by viewModels()
    override val binding by viewBinding(ActivityCreateEditProductBinding::inflate)

    private var chosenImage: Uri? = null

    private val startForImagePickingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    chosenImage = data?.data!!
                    binding.ivProductImage.setImageURI(chosenImage)
                }
                ImagePicker.RESULT_ERROR -> {
                    showErrorMsg(ImagePicker.getError(data))
                }
                else -> {
                    showWarningMsg(getString(R.string.image_picking_cancelled))
                }
            }
        }

    override fun onActivityCreated() {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            lblProductPrice.text = getString(R.string.product_price_in_currency)

            viewModel.productModel?.let {
                if (!it.imgUrl.isNullOrEmpty())
                    Picasso.get()
                        .load(it.imgUrl)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductImage)
                etProductName.setText(it.name)
                etProductPrice.setText(it.wholesalePrice?.toInt().toString())
                etCommission.setText(it.commissionValue?.toInt().toString())
            }

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
            vChooseImage.setOnClickListener {
                startImagePicking()
            }
            btnSubmit.setOnClickListener {
                startValidationAndPrepareData()
            }
        }
    }

    private fun startImagePicking() {
        ImagePicker.with(this).crop(1f, 1f).compress(1024).createIntent {
            startForImagePickingResult.launch(it)
        }
    }

    private fun startValidationAndPrepareData() {
        // Images
        if (chosenImage == null && viewModel.productModel?.imgUrl == null) {
            showErrorMsg(getString(R.string.please_pick_an_image_first))
            return
        }
        // Name
        val name = binding.etProductName.text.toString().trim()
        if (name.isEmpty()) {
            showErrorMsg(getString(R.string.name_field_cannot_be_empty))
            return
        }

        // Price
        val price = try {
            binding.etProductPrice.text.toString().trim()
                .convertArabicNumbersIfExist().getDigit().toFloat()
        } catch (e: Exception) {
            e.printStackTrace(); -1.0f
        }
        if (price < 0.0f) {
            showErrorMsg(getString(R.string.invalid_price_value))
            return
        }

        // Commission
        val commission = try {
            binding.etCommission.text.toString().trim()
                .convertArabicNumbersIfExist().getDigit().toFloat()
        } catch (e: Exception) {
            e.printStackTrace(); -1.0f
        }
        if (commission < 0.0f) {
            showErrorMsg(getString(R.string.invalid_commission_value))
            return
        }

        if (chosenImage != null)
            viewModel.uploadProductImageAndGetUrl(chosenImage!!)
                .observe(this) { imageUrl ->
                    createProduct(name, imageUrl ?: "", price, commission)
                }
        else
            createProduct(name, viewModel.productModel?.imgUrl ?: "", price, commission)

    }

    private fun createProduct(
        name: String,
        imgUrl: String,
        wholesalePrice: Float,
        commissionValue: Float,
    ) {
        viewModel.createProduct(name, imgUrl, wholesalePrice, commissionValue).observe(this) {
            showSuccessMsg(
                getString(
                    if (viewModel.productModel == null) R.string.product_created_successfully
                    else R.string.product_updated_successfully
                )
            )
            finish()
        }
    }
}