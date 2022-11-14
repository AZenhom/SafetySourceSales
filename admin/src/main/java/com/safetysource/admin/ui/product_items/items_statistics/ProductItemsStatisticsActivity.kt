package com.safetysource.admin.ui.product_items.items_statistics

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.safetysource.admin.databinding.ActivityProductItemsStatisticsBinding
import com.safetysource.admin.ui.product_items.item_details.ProductItemDetailsActivity
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.dialogs.InfoDialog
import com.safetysource.core.utils.getFilePath
import com.safetysource.core.utils.getLocalizedComma
import com.safetysource.core.utils.registerPermissions
import com.safetysource.data.model.ProductItemModel
import com.safetysource.data.model.ProductItemState
import com.safetysource.data.model.ProductModel
import dagger.hilt.android.AndroidEntryPoint
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

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

    private val excelFilePermissionLauncher =
        registerPermissions(
            onPermissionGranted = {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/excel"
                val mimeTypes = arrayOf(
                    "application/excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-excel"
                )
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                excelFilePickingLauncher.launch(
                    Intent.createChooser(intent, getString(R.string.select_excel_file))
                )
            },
            onPermissionDenied = {
                it.forEach { e ->
                    showErrorMsg(e)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                        val uri = Uri.fromParts("package", this.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            })

    private val excelFilePickingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val fileUri = result.data?.data
                if (fileUri == null) showErrorMsg(getString(R.string.something_went_wrong))
                else parseExcelFile(fileUri)
            }
        }

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

    private val productItemsList = mutableListOf<ProductItemModel>()

    override fun onActivityCreated() {
        initViews()
        getProductItemStatistics()
    }

    @SuppressLint("InlinedApi")
    private fun initViews() {
        with(binding) {
            registerToolBarOnBackPressed(toolbar)
            btnImportExcel.setOnClickListener {
                excelFilePermissionLauncher.launch(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        )
                    } else {
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        )
                    }

                )
            }
            fabAdd.setOnClickListener {
                val scanOptions = ScanOptions()
                scanOptions.setOrientationLocked(false)
                barcodeLauncher.launch(scanOptions)
            }
        }
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
        viewModel.getProductItemBySerial(serial).observe(this) { pair ->
            if (pair.second == null)
                showAddProductItemDialog(serial)
            else if (pair.first == null)
                showErrorMsg(getString(R.string.serial_not_associated_with_any_product))
            else
                startActivity(
                    ProductItemDetailsActivity.getIntent(
                        this,
                        pair.first!!,
                        pair.second!!
                    )
                )
        }
    }

    private fun parseExcelFile(fileUri: Uri) {
        val serialList: MutableList<String> = mutableListOf()
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(File(fileUri.getFilePath(this)))
            val sheet = XSSFWorkbook(fileInputStream).getSheetAt(0)
            for (row in sheet) {
                if (row.rowNum == 0) continue // Skip first row (Column Title)
                val cell = row.getCell(0)
                if (cell.cellTypeEnum == CellType.STRING && !cell.stringCellValue.isNullOrEmpty()) {
                    serialList.add(cell.stringCellValue)
                }
            }
            if (serialList.isEmpty()) showErrorMsg(getString(R.string.no_serials_in_excel_file))
            else searchSerials(serialList)
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorMsg(getString(R.string.something_went_wrong))
        } finally {
            fileInputStream?.close()
        }
    }

    private fun searchSerials(serials: MutableList<String>) {
        viewModel.getProductItemsBySerials(serials).observe(this) { results ->
            serials.removeIf { serial -> results.find { it.serial == serial } != null }
            if (serials.isEmpty())
                showErrorMsg(getString(R.string.all_serials_exist))
            else {
                var dialogMessage =
                    getString(R.string.serials_to_be_registered, serials.size.toString())
                if (results.isNotEmpty()) {
                    dialogMessage += getLocalizedComma(this)
                    dialogMessage += getString(
                        R.string.serials_to_be_ignored,
                        results.size.toString()
                    )
                }
                showAddProductItemsDialog(dialogMessage, serials)
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

    private fun showAddProductItemsDialog(dialogMessage: String, serials: List<String>) {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = dialogMessage,
            confirmText = getString(R.string.register_serials),
            onConfirm = { infoDialog?.dismiss(); registerNewProductsSerials(serials) },
            isCancelable = true
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    private fun registerNewProductSerial(serial: String) {
        viewModel.createProductItem(serial).observe(this) {
            showSuccessMsg(getString(R.string.product_serial_registered_successfully))
            getProductItemStatistics()
        }
    }

    private fun registerNewProductsSerials(serials: List<String>) {
        viewModel.createProductItems(serials).observe(this) {
            showSuccessMsg(getString(R.string.product_serials_registered_successfully))
            getProductItemStatistics()
        }
    }
}