package com.safetysource.appadmin.ui.admins

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.activity.viewModels
import com.safetysource.appadmin.databinding.ActivityCreateEditAdminBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.utils.PhoneNumberUtils
import com.safetysource.core.utils.PhoneNumberUtils.PhoneNumberUtils.isNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEditAdminActivity :
    BaseActivity<ActivityCreateEditAdminBinding, CreateEditAdminViewModel>() {

    companion object {
        fun getIntent(context: Context) =
            Intent(context, CreateEditAdminActivity::class.java)
    }

    override val viewModel: CreateEditAdminViewModel by viewModels()
    override val binding by viewBinding(ActivityCreateEditAdminBinding::inflate)

    private val phoneTextWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.trim().count() > 15) {
                    binding.tilAdminPhone.error = getString(R.string.error_invalid_phone)
                    binding.etAdminPhone.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(16))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilAdminPhone.error = null
            }
        }
    }

    override fun onActivityCreated() {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            // Phone No
            etAdminPhone.addTextChangedListener(phoneTextWatcher)

            // Submit
            btnSubmit.setOnClickListener {
                validateAndRegister()
            }
        }
    }

    private fun validateAndRegister() {
        val adminName = binding.etAdminName.text.toString().trim()
        if (adminName.isEmpty()) {
            showErrorMsg(getString(R.string.name_field_cannot_be_empty))
            return
        }

        val phoneNo = validatePhoneNumber() ?: return

        viewModel.createNewAdmin(phoneNo, adminName).observe(this) {
            showSuccessMsg(getString(R.string.admin_registered_successfully))
            finish()
        }
    }

    private fun validatePhoneNumber(): String? {
        val selectedCountryIso = binding.countryPicker.selectedCountryNameCode
        val phoneText = binding.etAdminPhone.text.toString().trim()
        val phoneNumberObject =
            PhoneNumberUtils.getPhoneIfValid(this, selectedCountryIso, phoneText)
        if (phoneNumberObject.isNull()) {
            showWarningMsg(getString(R.string.error_invalid_phone))
            return null
        }
        return "+" + phoneNumberObject?.countryCode.toString() + phoneNumberObject?.nationalNumber.toString()
    }
}