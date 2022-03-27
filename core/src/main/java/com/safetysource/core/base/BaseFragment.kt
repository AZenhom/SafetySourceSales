package com.safetysource.core.base

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.safetysource.core.ui.LoaderDialogFragment

import es.dmoral.toasty.Toasty

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(@LayoutRes layoutId: Int) :
    Fragment(layoutId) {

    abstract val viewModel: VM
    abstract val binding: VB

    protected abstract fun onViewCreated()

    private lateinit var loadingDialog: LoaderDialogFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoaderDialogFragment()

        initObservers()
        onViewCreated()
    }

    private fun initObservers() {
        viewModel.errorMsgLiveData.observe(viewLifecycleOwner) { showErrorMsg(it) }
        viewModel.errorMsgResourceLiveData.observe(
            viewLifecycleOwner,
            { showErrorMsg(getString(it)) })

        viewModel.successMsgLiveData.observe(viewLifecycleOwner, { showSuccessMsg(it) })
        viewModel.successMsgResourceLiveData.observe(
            viewLifecycleOwner,
            { showSuccessMsg(getString(it)) })


        viewModel.loadingLiveData.observe(viewLifecycleOwner) {
            if (it)
                showLoading()
            else
                hideLoading()
        }
    }

    fun shoWarningMsg(msg: String) {
        if (isAdded)
            Toasty.warning(requireContext(), msg, Toasty.LENGTH_LONG).show()
    }

    fun showErrorMsg(msg: String) {
        if (isAdded)
            Toasty.error(requireContext(), msg, Toasty.LENGTH_LONG).show()
    }

    fun showSuccessMsg(msg: String) {
        if (isAdded)
            Toasty.success(requireContext(), msg, Toasty.LENGTH_LONG).show()
    }

    fun showLoading() {
        hideKeyboard()
        if (!loadingDialog.isVisible)
            loadingDialog.show(parentFragmentManager, LoaderDialogFragment.TAG)
    }


    fun hideLoading() {
        loadingDialog.dismiss()
    }

    protected open fun showKeyboard(focusedView: View) {
        try {
            val imm =
                activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open fun hideKeyboard() {
        try {
            val imm =
                activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity?.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
        FragmentViewBindingDelegate(this, viewBindingFactory)

}