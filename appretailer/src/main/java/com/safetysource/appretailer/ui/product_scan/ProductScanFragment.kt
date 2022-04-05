package com.safetysource.appretailer.ui.product_scan

import android.Manifest
import androidx.fragment.app.viewModels
import com.budiyev.android.codescanner.CodeScanner
import com.safetysource.appretailer.R
import com.safetysource.appretailer.databinding.FragmentProductScanBinding
import com.safetysource.appretailer.ui.product_items.ProductItemDetailsActivity
import com.safetysource.core.base.BaseFragment
import com.safetysource.core.ui.makeGone
import com.safetysource.core.ui.makeVisible
import com.safetysource.core.utils.registerPermissions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductScanFragment :
    BaseFragment<FragmentProductScanBinding, ProductScanViewModel>(R.layout.fragment_product_scan) {

    override val viewModel: ProductScanViewModel by viewModels()
    override val binding by viewBinding(FragmentProductScanBinding::bind)

    private lateinit var codeScanner: CodeScanner

    private val startCameraIntent = registerPermissions(
        onPermissionGranted = { switchViews(true) },
        onPermissionDenied = { it.forEach { error -> showErrorMsg(error) } }
    )

    override fun onViewCreated() {
        initViews()
        initScanner()
    }

    override fun onPause() {
        switchViews(false)
        super.onPause()
    }

    private fun initViews() {
        with(binding) {
            btnStartScanning.setOnClickListener {
                startCameraIntent.launch(arrayOf(Manifest.permission.CAMERA))
            }

            btnCloseScanner.setOnClickListener {
                switchViews(false)
            }
        }
    }

    private fun initScanner() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.setDecodeCallback {
            switchViews(false)
            searchSerial(it.text)
        }
    }

    private fun switchViews(openScanner: Boolean) {
        with(binding) {
            if (openScanner) {
                grIllustrativeContent.makeGone()
                grScannerContent.makeVisible()
                codeScanner.releaseResources()
            } else {
                grIllustrativeContent.makeVisible()
                grScannerContent.makeGone()
                codeScanner.startPreview()
            }
        }
    }

    private fun searchSerial(serial: String) {
        viewModel.getProductItemBySerial(serial).observe(viewLifecycleOwner) { productItem ->
            productItem?.let {
                startActivity(ProductItemDetailsActivity.getIntent(requireContext(), it))
            }
        }
    }

}