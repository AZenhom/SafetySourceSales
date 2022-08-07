package com.safetysource.appadmin.ui.retailers.create_edit_retailer

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityCreateEditRetailerBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.makeGone
import com.safetysource.core.utils.PhoneNumberUtils
import com.safetysource.core.utils.PhoneNumberUtils.PhoneNumberUtils.isNull
import com.safetysource.data.model.RetailerModel
import com.safetysource.data.model.TeamModel
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
        initViews()
    }

    private fun initViews() {
        val isEditMode = viewModel.retailerToEdit != null
        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            if (isEditMode) {
                etRetailerPhone.inputType = InputType.TYPE_NULL
                lblCountry.makeGone()
                countryPicker.makeGone()

                etRetailerName.setText(viewModel.retailerToEdit?.name)
                etRetailerPhone.setText(viewModel.retailerToEdit?.phoneNo)
            }

            // Team Name (Not changeable in both cases)
            etRetailerTeam.inputType = InputType.TYPE_NULL
            etRetailerTeam.setText(viewModel.teamModel?.name)

            // Phone No
            etRetailerPhone.addTextChangedListener(phoneTextWatcher)

            // Submit
            btnSubmit.setOnClickListener {
                if (isEditMode) updateRetailer()
                else validateAndRegister()
            }
        }
    }

    private fun updateRetailer() {
        viewModel.updateRetailer().observe(this) {
            showSuccessMsg(getString(R.string.retailer_updated_successfully))
            finish()
        }
    }

    private fun validateAndRegister() {
        val retailerName = binding.etRetailerName.text.toString().trim()
        if (retailerName.isEmpty()) {
            showErrorMsg(getString(R.string.name_field_cannot_be_empty))
            return
        }

        val phoneNo = validatePhoneNumber() ?: return

        viewModel.createNewRetailer(phoneNo, phoneNo, retailerName).observe(this) {
            showSuccessMsg(getString(R.string.retailer_registered_successfully))
            finish()
        }
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
}