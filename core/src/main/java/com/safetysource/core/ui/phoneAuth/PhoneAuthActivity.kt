package com.safetysource.core.ui.phoneAuth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.activity.viewModels
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.databinding.ActivityPhoneAuthBinding
import com.safetysource.core.ui.makeGone
import com.safetysource.core.ui.makeVisible
import com.safetysource.core.utils.PhoneNumberUtils
import com.safetysource.core.utils.PhoneNumberUtils.PhoneNumberUtils.isNull
import com.safetysource.data.model.AccountType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneAuthActivity : BaseActivity<ActivityPhoneAuthBinding, PhoneAuthViewModel>() {

    companion object {
        const val KEY_ACCOUNT_TYPE = "KEY_ACCOUNT_TYPE"

        fun getIntent(context: Context, accountType: AccountType): Intent {
            return Intent(context, PhoneAuthActivity::class.java).apply {
                putExtra(KEY_ACCOUNT_TYPE, accountType)
            }
        }
    }

    override val viewModel: PhoneAuthViewModel by viewModels()
    override val binding: ActivityPhoneAuthBinding by viewBinding(ActivityPhoneAuthBinding::inflate)

    private val phoneTextWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.trim().count() > 15) {
                    binding.tilPhone.error = getString(R.string.error_invalid_phone)
                    binding.etPhone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilPhone.error = null
            }
        }
    }

    override fun onActivityCreated() {
        initObservers()
        initViews()
    }

    private fun initObservers() {
        with(viewModel) {
            onSmsCodeSentLiveData.observe(this@PhoneAuthActivity) {
                setContentMode(ContentType.SMS_CODE_VERIFICATION)
            }
            onPhoneAuthCompletedLiveData.observe(this@PhoneAuthActivity){
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initViews() {
        setContentMode(ContentType.PHONE_VERIFICATION)
        with(binding) {
            etPhone.addTextChangedListener(phoneTextWatcher)
            btnPhoneVerify.setOnClickListener {
                validatePhoneNumber()
            }
            pinView.setOtpCompletionListener {
                startSmsCodeVerification()
            }
            btnCodeVerify.setOnClickListener {
                startSmsCodeVerification()
            }
        }
    }

    private fun setContentMode(contentType: ContentType) {
        when (contentType) {
            ContentType.PHONE_VERIFICATION -> {
                binding.grPhoneNumber.makeVisible()
                binding.grSms.makeGone()
            }
            ContentType.SMS_CODE_VERIFICATION -> {
                binding.grPhoneNumber.makeGone()
                binding.grSms.makeVisible()
            }
        }
    }

    private fun validatePhoneNumber() {
        val selectedCountryIso = binding.countryPicker.selectedCountryNameCode
        val phoneText = binding.etPhone.text.toString().trim()
        val phoneNumberObject =
            PhoneNumberUtils.getPhoneIfValid(this, selectedCountryIso, phoneText)
        if (phoneNumberObject.isNull()) {
            showWarningMsg(getString(R.string.error_invalid_phone))
            return
        }
        val fullPhoneNumber =
            "+" + phoneNumberObject?.countryCode.toString() + phoneNumberObject?.nationalNumber.toString()
        startPhoneVerification(fullPhoneNumber)
    }

    private fun startPhoneVerification(phoneNumber: String) {
        viewModel.startPhoneVerification(this, phoneNumber)
    }

    private fun startSmsCodeVerification() {
        val code = binding.pinView.text.toString()
        if (code.length != binding.pinView.itemCount) {
            showWarningMsg(getString(R.string.invalid_verification_code))
            return
        }
        viewModel.signInUsingSentCode(this, code)
    }
}

private enum class ContentType {
    PHONE_VERIFICATION,
    SMS_CODE_VERIFICATION
}