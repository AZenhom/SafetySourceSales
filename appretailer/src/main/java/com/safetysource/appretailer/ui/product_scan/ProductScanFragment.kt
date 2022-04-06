package com.safetysource.appretailer.ui.product_scan

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.safetysource.appretailer.databinding.FragmentProductScanBinding
import com.safetysource.appretailer.ui.product_items.ProductItemDetailsActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductScanFragment :
    BaseFragment<FragmentProductScanBinding, ProductScanViewModel>(com.safetysource.appretailer.R.layout.fragment_product_scan) {

    override val viewModel: ProductScanViewModel by viewModels()
    override val binding by viewBinding(FragmentProductScanBinding::bind)

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null)
            showErrorMsg(getString(R.string.scan_cancelled))
        else
            searchSerial(result.contents)
    }

    override fun onViewCreated() {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            btnStartScanning.setOnClickListener {
                barcodeLauncher.launch(ScanOptions())
            }
        }
    }

    private fun searchSerial(serial: String) {
        viewModel.getProductItemBySerial(serial).observe(viewLifecycleOwner) { productItem ->
            if(productItem == null)
                showErrorMsg(getString(R.string.serial_not_associated_with_any_product))
            else
                startActivity(ProductItemDetailsActivity.getIntent(requireContext(), productItem))
        }
    }

}