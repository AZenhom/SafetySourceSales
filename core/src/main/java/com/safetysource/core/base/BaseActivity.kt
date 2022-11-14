package com.safetysource.core.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.safetysource.core.R
import com.safetysource.core.ui.LoaderDialogFragment
import es.dmoral.toasty.Toasty


@SuppressLint("SourceLockedOrientationActivity")
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    abstract val viewModel: VM
    abstract val binding: VB

    abstract fun onActivityCreated()


    private lateinit var loadingDialog: LoaderDialogFragment
    var doubleBackToExitPressedOnce = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Force app in portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(binding.root)

        loadingDialog = LoaderDialogFragment()

        initObservers()
        onActivityCreated()
    }

    private fun initObservers() {
        viewModel.errorMsgLiveData.observe(this) { showErrorMsg(it) }
        viewModel.errorMsgResourceLiveData.observe(this) { showErrorMsg(getString(it)) }

        viewModel.successMsgLiveData.observe(this) { showSuccessMsg(it) }
        viewModel.successMsgResourceLiveData.observe(this) { showSuccessMsg(getString(it)) }

        viewModel.loadingLiveData.observe(this) {
            if (it)
                showLoading()
            else
                hideLoading()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    fun showWarningMsg(msg: String) {
        Toasty.warning(this, msg, Toasty.LENGTH_LONG).show()
    }

    fun showErrorMsg(msg: String) {
        Toasty.error(this, msg, Toasty.LENGTH_LONG).show()
    }

    fun showSuccessMsg(msg: String) {
        Toasty.success(this, msg, Toasty.LENGTH_LONG).show()
    }

    fun showLoading() {
        hideKeyboard()
        if (!loadingDialog.isVisible)
            loadingDialog.show(supportFragmentManager, LoaderDialogFragment.TAG)
    }

    fun hideLoading() {
        try {
            loadingDialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open fun setTitleWithBack(title: String) {
        setTitle(title)
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    protected open fun showKeyboard(focusedView: View) {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            var view = currentFocus
            if (view == null) {
                view = View(this)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce)
                    finish()
                else {
                    doubleBackToExitPressedOnce = true
                    Toasty.info(this@BaseActivity, getString(R.string.click_twice)).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        }

    protected fun registerToolBarOnBackPressed(toolbar: MaterialToolbar) {
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) =
        lazy(LazyThreadSafetyMode.NONE) {
            bindingInflater.invoke(layoutInflater)
        }
}