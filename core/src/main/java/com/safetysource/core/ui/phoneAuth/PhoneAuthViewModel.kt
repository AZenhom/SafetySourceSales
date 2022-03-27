package com.safetysource.core.ui.phoneAuth

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hadilq.liveevent.LiveEvent
import com.safetysource.core.base.BaseViewModel
import com.safetysource.data.model.AccountType
import com.safetysource.data.repository.AdminRepository
import com.safetysource.data.repository.RetailerRepository
import com.safetysource.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import com.safetysource.core.R
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class PhoneAuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
    private val retailerRepository: RetailerRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val TAG = "PhoneAuthViewModel"
    }

    private val accountType: AccountType? = savedStateHandle[PhoneAuthActivity.KEY_ACCOUNT_TYPE]

    private var activity: PhoneAuthActivity? = null
    private val auth = FirebaseAuth.getInstance()
    private lateinit var verificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) { // On Auto Verify
            Log.d(TAG, "onVerificationCompleted")
            startSigningInUsingCredentials(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) { // Invalid Phone format
            Log.d(TAG, "onVerificationFailed")
            hideLoading()
            showErrorMsg(R.string.invalid_phone_format)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent")
            this@PhoneAuthViewModel.verificationId = verificationId
            resendToken = token
            hideLoading()
            _onSmsCodeSentLiveData.value = true
        }
    }

    private val _onSmsCodeSentLiveData: LiveEvent<Boolean> = LiveEvent()
    val onSmsCodeSentLiveData: LiveData<Boolean> get() = _onSmsCodeSentLiveData

    private val _onPhoneAuthCompletedLiveData: LiveEvent<Boolean> = LiveEvent()
    val onPhoneAuthCompletedLiveData: LiveData<Boolean> get() = _onPhoneAuthCompletedLiveData

    init {
        auth.useAppLanguage()
    }

    fun startPhoneVerification(activity: PhoneAuthActivity, phoneNumber: String) {
        Log.d(TAG, "startPhoneVerification")
        this.activity = activity
        showLoading()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInUsingSentCode(activity: PhoneAuthActivity, sentCode: String) {
        this.activity = activity
        Log.d(TAG, "signInUsingSentCode")
        val credentials = PhoneAuthProvider.getCredential(verificationId, sentCode)
        startSigningInUsingCredentials(credentials)
    }

    private fun startSigningInUsingCredentials(credentials: PhoneAuthCredential) {
        Log.d(TAG, "startSigningInUsingCredentials")
        if (activity == null)
            return
        showLoading()
        auth.signInWithCredential(credentials)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful && task.result?.user != null) {
                    Log.d(TAG, "startSigningInUsingCredentials Success")
                    val user = task.result!!.user!!
                    saveUserCredentialsAndCheckIfRegistered(user)
                } else { // Wrong Code
                    Log.d(TAG, "startSigningInUsingCredentials Failure" + task.exception.toString())
                    hideLoading()
                    showErrorMsg(R.string.invalid_verification_code)
                }
            }
    }

    private fun saveUserCredentialsAndCheckIfRegistered(user: FirebaseUser) = safeLauncher {
        Log.d(TAG, "saveUserCredentialsAndCheckIfRegistered User: " + user.uid)
        userRepository.setSignedIn(true)
        userRepository.setUserId(user.uid)
        userRepository.setUserSigningInPhone(user.phoneNumber ?: "")

        when (accountType) {
            AccountType.ADMIN -> {
                val response = adminRepository.getAdminById(user.uid)
                adminRepository.setCurrentAdminModel(response.data)
            }
            AccountType.RETAILER -> {
                val response = retailerRepository.getRetailerById(user.uid)
                retailerRepository.setCurrentRetailerModel(response.data)
            }
            else -> Unit
        }
        _onPhoneAuthCompletedLiveData.value = true
    }

    override fun onCleared() {
        activity = null
        super.onCleared()
    }
}