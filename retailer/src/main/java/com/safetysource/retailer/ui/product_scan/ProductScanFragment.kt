package com.safetysource.retailer.ui.product_scan

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.safetysource.retailer.databinding.FragmentProductScanBinding
import com.safetysource.retailer.ui.product_item.ProductItemDetailsActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductScanFragment :
    BaseFragment<FragmentProductScanBinding, ProductScanViewModel>(com.safetysource.retailer.R.layout.fragment_product_scan) {

    override val viewModel: ProductScanViewModel by viewModels()
    override val binding by viewBinding(FragmentProductScanBinding::bind)

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        try {
            if (result.contents == null)
                showErrorMsg(getString(R.string.scan_cancelled))
            else
                searchSerial(result.contents)
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorMsg(getString(R.string.something_went_wrong))
        }
    }

    override fun onViewCreated() {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            btnStartScanning.setOnClickListener {
                val scanOptions = ScanOptions()
                scanOptions.setOrientationLocked(false)
                barcodeLauncher.launch(scanOptions)
            }
        }
    }

    private fun searchSerial(serial: String) {
        viewModel.getProductItemBySerial(serial).observe(viewLifecycleOwner) { productItem ->
            if (productItem == null)
                showErrorMsg(getString(R.string.serial_not_associated_with_any_product))
            else
                startActivity(ProductItemDetailsActivity.getIntent(requireContext(), productItem))
        }
    }

}