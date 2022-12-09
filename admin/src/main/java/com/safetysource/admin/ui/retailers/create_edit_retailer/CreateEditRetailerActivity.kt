package com.safetysource.admin.ui.retailers.create_edit_retailer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.safetysource.admin.databinding.ActivityCreateEditRetailerBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.makeGone
import com.safetysource.core.ui.sheets.MultipleSelectListSheet
import com.safetysource.core.utils.PhoneNumberUtils
import com.safetysource.core.utils.PhoneNumberUtils.PhoneNumberUtils.isNull
import com.safetysource.data.model.ProductModel
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.TeamModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEditRetailerActivity :
    BaseActivity<ActivityCreateEditRetailerBinding, CreateEditRetailerViewModel>() {

    companion object {
        const val TEAM_MODEL = "TEAM_MODEL"
        const val RETAILER_TO_EDIT = "RETAILER_TO_EDIT"
        fun getIntent(
            context: Context,
            teamModel: TeamModel,
            retailerToEdit: RetailerModel? = null
        ) =
            Intent(context, CreateEditRetailerActivity::class.java).apply {
                putExtra(TEAM_MODEL, teamModel)
                putExtra(RETAILER_TO_EDIT, retailerToEdit)
            }
    }

    override val viewModel: CreateEditRetailerViewModel by viewModels()
    override val binding by viewBinding(ActivityCreateEditRetailerBinding::inflate)

    private var productsList: List<ProductModel> = emptyList()
    private var restrictedProductsList: List<ProductModel> = emptyList()

    private lateinit var anyText: String

    private var chosenImage: Uri? = null

    private val startForImagePickingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    chosenImage = data?.data!!
                    binding.ivRetailerImage.setImageURI(chosenImage)
                }
                ImagePicker.RESULT_ERROR -> {
                    showErrorMsg(ImagePicker.getError(data))
                }
                else -> {
                    showWarningMsg(getString(R.string.image_picking_cancelled))
                }
            }
        }

    private val phoneTextWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.trim().count() > 15) {
                    binding.tilRetailerPhone.error = getString(R.string.error_invalid_phone)
                    binding.etRetailerPhone.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(16))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilRetailerPhone.error = null
            }
        }
    }

    override fun onActivityCreated() {
        anyText = getString(R.string.any)
        initViews()
        initObservers()
    }

    private fun initViews() {
        val isEditMode = viewModel.retailerToEdit != null
        with(binding) {
            registerToolBarOnBackPressed(toolbar)

            if (isEditMode) {
                tvTitle.text = getString(R.string.update_retailer_details)
                etRetailerPhone.isClickable = false
                etRetailerPhone.isFocusable = false
                etRetailerPhone.inputType = InputType.TYPE_NULL
                lblCountry.makeGone()
                countryPicker.makeGone()
                viewModel.retailerToEdit?.let { retailer ->
                    etRetailerName.setText(retailer.name)
                    etRetailerPhone.setText(retailer.phoneNo)
                    if (!retailer.imgUrl.isNullOrEmpty())
                        Picasso.get()
                            .load(retailer.imgUrl)
                            .error(R.drawable.ic_image_placeholder)
                            .into(ivRetailerImage)
                }
            }

            // Team Name (Not changeable in both cases)
            etRetailerTeam.inputType = InputType.TYPE_NULL
            etRetailerTeam.isClickable = false
            etRetailerTeam.isFocusable = false
            etRetailerTeam.setText(viewModel.teamModel?.name)

            // Phone No
            etRetailerPhone.addTextChangedListener(phoneTextWatcher)

            // Allowed Products
            binding.tvProduct.text = anyText
            clProduct.setOnClickListener {
                MultipleSelectListSheet(
                    itemsList = productsList.toMutableList(),
                    selectedItems = restrictedProductsList.toMutableList(),
                    sheetTitle = getString(R.string.products),
                    sheetSubTitle = getString(R.string.please_pick_set_of_products)
                ) { viewModel.setRestrictedProducts(it.filterNotNull()) }
                    .show(supportFragmentManager, MultipleSelectListSheet.TAG)
            }

            // Click Listeners
            vChooseImage.setOnClickListener {
                startImagePicking()
            }
            btnSubmit.setOnClickListener {
                if (isEditMode) updateRetailer()
                else validateAndRegister()
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            productsLiveData.observe(this@CreateEditRetailerActivity) { productsList = it }
            restrictedProductsLiveData.observe(this@CreateEditRetailerActivity) {
                restrictedProductsList = it
                binding.tvProduct.text = if (it.isEmpty()) anyText
                else getString(R.string.n_selections, it.size.toString())
            }
        }
    }

    private fun startImagePicking() {
        ImagePicker.with(this).crop(1f, 1f).compress(1024).createIntent {
            startForImagePickingResult.launch(it)
        }
    }

    private fun validateName(): String? {
        val retailerName = binding.etRetailerName.text.toString().trim()
        if (retailerName.isEmpty()) {
            showErrorMsg(getString(R.string.name_field_cannot_be_empty))
            return null
        }
        return retailerName
    }

    private fun validatePhoneNumber(): String? {
        val selectedCountryIso = binding.countryPicker.selectedCountryNameCode
        val phoneText = binding.etRetailerPhone.text.toString().trim()
        val phoneNumberObject =
            PhoneNumberUtils.getPhoneIfValid(this, selectedCountryIso, phoneText)
        if (phoneNumberObject.isNull()) {
            showWarningMsg(getString(R.string.error_invalid_phone))
            return null
        }
        return "+" + phoneNumberObject?.countryCode.toString() + phoneNumberObject?.nationalNumber.toString()
    }

    private fun updateRetailer() {
        val retailerName = validateName() ?: return
        if (chosenImage != null)
            viewModel.uploadProductImageAndGetUrl(chosenImage!!).observe(this) {
                viewModel.updateRetailer(retailerName, it).observe(this) {
                    showSuccessMsg(getString(R.string.retailer_updated_successfully))
                    finish()
                }
            }
        else
            viewModel.updateRetailer(retailerName).observe(this) {
                showSuccessMsg(getString(R.string.retailer_updated_successfully))
                finish()
            }
    }

    private fun validateAndRegister() {
        val retailerName = validateName() ?: return
        val phoneNo = validatePhoneNumber() ?: return
        if (chosenImage != null)
            viewModel.uploadProductImageAndGetUrl(chosenImage!!).observe(this) {
                viewModel.createNewRetailer(phoneNo, phoneNo, retailerName, it).observe(this) {
                    showSuccessMsg(getString(R.string.retailer_registered_successfully))
                    finish()
                }
            }
        else
            viewModel.createNewRetailer(phoneNo, phoneNo, retailerName).observe(this) {
                showSuccessMsg(getString(R.string.retailer_registered_successfully))
                finish()
            }
    }
}