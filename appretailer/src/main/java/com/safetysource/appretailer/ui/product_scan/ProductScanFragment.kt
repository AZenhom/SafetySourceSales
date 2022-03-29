package com.safetysource.appretailer.ui.product_scan

import androidx.fragment.app.viewModels
import com.safetysource.appretailer.R
import com.safetysource.appretailer.databinding.FragmentProductScanBinding
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductScanFragment :
    BaseFragment<FragmentProductScanBinding, ProductScanViewModel>(R.layout.fragment_transactions) {

    override val viewModel: ProductScanViewModel by viewModels()
    override val binding by viewBinding(FragmentProductScanBinding::bind)

    override fun onViewCreated() {

    }
}