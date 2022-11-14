package com.safetysource.admin.ui.product_categories.create_edit_category

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.safetysource.core.R
import com.safetysource.admin.databinding.ActivityCreateProductCategoryBinding
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.utils.convertArabicNumbersIfExist
import com.safetysource.core.utils.getDigit
import com.safetysource.data.model.ProductCategoryModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class CreateEditProductCategoryActivity :
    BaseActivity<ActivityCreateProductCategoryBinding, CreateEditProductCategoryViewModel>() {


    companion object {
        const val CATEGORY_TO_EDIT = "CATEGORY_TO_EDIT"
        fun getIntent(context: Context, categoryToEdit: ProductCategoryModel? = null) =
            Intent(context, CreateEditProductCategoryActivity::class.java).apply {
                putExtra(CATEGORY_TO_EDIT, categoryToEdit)
            }
    }

    override val viewModel: CreateEditProductCategoryViewModel by viewModels()
    override val binding by viewBinding(ActivityCreateProductCategoryBinding::inflate)

    private var chosenPhotoUri: Uri? = null
    private val startForImagePickingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    chosenPhotoUri = fileUri
                    binding.ivCategory.setImageURI(fileUri)
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
            registerToolBarOnBackPressed(toolbar)

            // Category to edit data
            viewModel.productCategoryModel?.let {
                etCategoryName.setText(it.name)
                etCategoryRank.setText(it.rank.toString())
                it.imgUrl?.let { imgUrl ->
                    Picasso.get()
                        .load(imgUrl)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivCategory)
                }
            }

            ivChoosePhoto.setOnClickListener {
                startImagePicking()
            }
            btnSubmit.setOnClickListener {
                startValidationAndPrepareData()
            }
        }
    }

    private fun startImagePicking() {
        ImagePicker.with(this).crop(5f, 2f).compress(1024).createIntent {
            startForImagePickingResult.launch(it)
        }
    }

    private fun startValidationAndPrepareData() {
        if (chosenPhotoUri == null && viewModel.productCategoryModel?.imgUrl == null) {
            showErrorMsg(getString(R.string.please_pick_an_image_first))
            return
        }

        val categoryName = binding.etCategoryName.text.toString().trim()
        if (categoryName.isEmpty()) {
            showErrorMsg(getString(R.string.name_field_cannot_be_empty))
            return
        }

        val categoryRank = try {
            binding.etCategoryRank.text.toString().trim()
                .convertArabicNumbersIfExist().getDigit().toInt()
        } catch (e: Exception) {
            e.printStackTrace(); 0
        }

        if (categoryRank <= 0) {
            showErrorMsg(getString(R.string.invalid_rank_value))
            return
        }

        // Upload image if necessary
        if (chosenPhotoUri != null)
            viewModel.uploadProductCategoryImageAndGetUrl(chosenPhotoUri!!)
                .observe(this) { downloadLink ->
                    submitData(downloadLink, categoryName, categoryRank)
                }
        else
            submitData(viewModel.productCategoryModel!!.imgUrl!!, categoryName, categoryRank)
    }

    private fun submitData(
        imageDownloadLink: String,
        categoryName: String,
        categoryRank: Int
    ) {
        viewModel.createProductCategory(
            imageDownloadLink,
            categoryName,
            categoryRank
        ).observe(this) {
            showSuccessMsg(
                getString(
                    if (viewModel.productCategoryModel == null) R.string.product_category_created_successfully
                    else R.string.product_category_updated_successfully
                )
            )
            finish()
        }
    }
}