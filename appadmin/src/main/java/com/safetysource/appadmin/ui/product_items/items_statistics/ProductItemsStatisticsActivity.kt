package com.safetysource.appadmin.ui.product_items.items_statistics

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.budiyev.android.codescanner.CodeScanner
import com.safetysource.appadmin.databinding.ActivityProductItemsStatisticsBinding
import com.safetysource.appadmin.ui.product_items.item_details.ProductItemDetailsActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.ui.makeGone
import com.safetysource.core.ui.makeVisible
import com.safetysource.core.utils.registerPermissions
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductItemState
import com.safetysource.data.model.ProductModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductItemsStatisticsActivity :
    BaseActivity<ActivityProductItemsStatisticsBinding, ProductItemStatisticsViewModel>() {

    companion object {
        const val PRODUCT_MODEL = "PRODUCT_MODEL"
        fun getIntent(context: Context, productModel: ProductModel) =
            Intent(context, ProductItemsStatisticsActivity::class.java).apply {
                putExtra(PRODUCT_MODEL, productModel)
            }
    }

    override val viewModel: ProductItemStatisticsViewModel by viewModels()
    override val binding by viewBinding(ActivityProductItemsStatisticsBinding::inflate)

    private lateinit var codeScanner: CodeScanner

    private val startCameraIntent = registerPermissions(
        onPermissionGranted = { switchViews(true) },
        onPermissionDenied = { it.forEach { error -> showErrorMsg(error) } }
    )

    private val productItemsList = mutableListOf<ProductItemModel>()

    override fun onActivityCreated() {
        initViews()
        initScanner()
    }

    override fun onPause() {
        switchViews(false)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        getProductItemStatistics()
    }

    private fun initViews() {
        with(binding) {
            fabAdd.setOnClickListener {

            }
            btnCloseScanner.setOnClickListener {
                switchViews(false)
            }
        }
    }

    private fun initScanner() {
        codeScanner = CodeScanner(this, binding.scannerView)
        codeScanner.setDecodeCallback {
            switchViews(false)
            searchSerial(it.text)
        }
    }

    private fun switchViews(openScanner: Boolean) {
        with(binding) {
            if (openScanner) {
                grMainContent.makeGone()
                grScannerContent.makeVisible()
                codeScanner.releaseResources()
            } else {
                grMainContent.makeVisible()
                grScannerContent.makeGone()
                codeScanner.startPreview()
            }
        }
    }

    private fun showAddProductItemDialog(serial: String) {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = getString(R.string.add_product_item_text),
            confirmText = getString(R.string.register_serial),
            onConfirm = { infoDialog?.dismiss(); registerNewProductSerial(serial) },
            isCancelable = true
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    private fun bindDataToViews() {
        with(binding) {
            tvProductName.text = viewModel.productModel?.name
            tvTotalSerials.text = productItemsList.size.toString()
            tvSoldCount.text =
                productItemsList.filter { it.state == ProductItemState.SOLD }.size.toString()
            tvUnsoldCount.text =
                productItemsList.filter { it.state == ProductItemState.PENDING_UNSELLING }.size.toString()
        }
    }

    private fun getProductItemStatistics() {
        viewModel.getProductItems().observe(this) {
            productItemsList.clear()
            productItemsList.addAll(it)
            bindDataToViews()
        }
    }

    private fun searchSerial(serial: String) {
        viewModel.getProductItemBySerial(serial).observe(this) { productItem ->
            if (productItem == null)
                showAddProductItemDialog(serial)
            else
                startActivity(ProductItemDetailsActivity.getIntent(this, productItem))
        }
    }

    private fun registerNewProductSerial(serial: String) {
        viewModel.createProductItem(serial).observe(this) {
            showSuccessMsg(getString(R.string.product_serial_registered_successfully))
            getProductItemStatistics()
        }
    }
}