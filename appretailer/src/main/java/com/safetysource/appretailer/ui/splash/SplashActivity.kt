package com.safetysource.appretailer.ui.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

import com.safetysource.core.R
import com.safetysource.appretailer.databinding.ActivitySplashBinding
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.InfoDialog
import com.safetysource.core.ui.phoneAuth.PhoneAuthActivity
import com.safetysource.data.model.AccountType
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    companion object {
        fun getIntent(context: Context) = Intent(context, SplashActivity::class.java)
    }

    private var loginResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                checkNavigation()
            else
                finish()
        }

    override val viewModel: SplashViewModel by viewModels()
    override val binding: ActivitySplashBinding by viewBinding(ActivitySplashBinding::inflate)

    override fun onActivityCreated() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        initAnimation()
        checkNavigation()
    }

    private fun initAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.splash)
        animation.interpolator = AccelerateInterpolator()
        animation.duration = 600
        binding.ivLogo.animation = animation
    }

    private fun checkNavigation() {
        viewModel.checkNavigation().observe(this) {
            when (it) {
                NavigationCases.REGISTERED -> openMainScreen()
                NavigationCases.NOT_PHONE_AUTHENTICATED -> openPhoneAuthScreen()
                NavigationCases.NOT_REGISTERED -> showNotRegisteredSheet()
                else -> Unit
            }
        }
    }

    private fun openMainScreen() {
        //startActivity(HostActivity.getIntent(this))
        finish()
    }

    private fun openPhoneAuthScreen() {
        loginResultLauncher.launch(PhoneAuthActivity.getIntent(this, AccountType.RETAILER))
    }

    private fun showNotRegisteredSheet() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = getString(R.string.retailer_not_registered),
            confirmText = getString(R.string.logout),
            cancelText = getString(R.string.close_app),
            onConfirm = { infoDialog?.dismiss();logOut() },
            onCancel = { infoDialog?.dismiss();finish() }
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    private fun logOut() {
        viewModel.logout().observe(this) {
            openPhoneAuthScreen()
        }
    }
}