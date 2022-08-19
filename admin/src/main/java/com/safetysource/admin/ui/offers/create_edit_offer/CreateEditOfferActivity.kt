package com.safetysource.admin.ui.offers.create_edit_offer

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.safetysource.admin.databinding.ActivityCreateEditOfferBinding
import com.safetysource.core.R
import com.safetysource.core.base.BaseActivity
import com.safetysource.core.ui.sheets.SelectListSheet
import com.safetysource.core.utils.convertArabicNumbersIfExist
import com.safetysource.core.utils.getDateText
import com.safetysource.core.utils.getDigit
import com.safetysource.data.model.OfferModel
import com.safetysource.data.model.ProductCategoryModel
import com.safetysource.data.model.ProductModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateEditOfferActivity :
    BaseActivity<ActivityCreateEditOfferBinding, CreateEditOfferViewModel>() {

    companion object {
        const val OFFER_TO_EDIT = "OFFER_TO_EDIT"
        fun getIntent(context: Context, offerToEdit: OfferModel? = null) =
            Intent(context, CreateEditOfferActivity::class.java).apply {
                putExtra(OFFER_TO_EDIT, offerToEdit)
            }
    }

    override val viewModel: CreateEditOfferViewModel by viewModels()
    override val binding by viewBinding(ActivityCreateEditOfferBinding::inflate)

    private var categoriesList: List<ProductCategoryModel> = emptyList()
    private var productsList: List<ProductModel> = emptyList()

    private lateinit var anyText: String
    private var chosenImage: Uri? = null

    private val startForImagePickingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    chosenImage = data?.data!!
                    binding.ivOffer.setImageURI(chosenImage)
                }
                ImagePicker.RESULT_ERROR -> {
                    showErrorMsg(ImagePicker.getError(data))
                }
                else -> {
                    showWarningMsg(getString(R.string.image_picking_cancelled))
                }
            }
        }

    override fun onActivityCreated() {
        anyText = getString(R.string.any)
        initViews()
        initObservers()
        viewModel.getInitialData()
    }

    private fun initViews() {
        with(binding) {
            // TextViews text initialization
            binding.tvCategory.text = anyText
            binding.tvProduct.text = anyText
            viewModel.offerModel?.let {
                it.imgUrl?.let { imgUrl ->
                    Picasso.get().load(imgUrl).error(R.drawable.ic_image_placeholder).into(ivOffer)
                }
                etOfferText.setText(it.text)
                etOfferClaimValue.setText(it.valPerRepeat?.toString())
                etSellCount.setText(it.neededSellCount?.toString())
                switchCanRepeat.isChecked = it.canRepeat == true

                // Disabling date views if in edit mode (Non Changeable)
                cvStartsAt.isEnabled = false
                cvExpiresAt.isEnabled = false
            }
            tvStartsAt.text = viewModel.startsAt.time.getDateText("EE, d MMM yyyy")
            tvExpiresAt.text = viewModel.expiresAt.time.getDateText("EE, d MMM yyyy")

            // Click Listeners
            toolbar.setNavigationOnClickListener { onBackPressed() }

            ivChoosePhoto.setOnClickListener {
                startImagePicking()
            }

            clCategory.setOnClickListener {
                SelectListSheet(
                    itemsList = categoriesList.toMutableList(),
                    anyItemObjectIfApplicable = ProductCategoryModel("-1", anyText),
                    selectedItem = viewModel.selectedCategoryLiveData.value,
                    sheetTitle = getString(R.string.product_categories),
                    sheetSubTitle = getString(R.string.please_pick_product_category)
                ) {
                    tvCategory.text = it?.name ?: anyText
                    tvProduct.text = anyText
                    viewModel.setCategory(it)
                }.show(supportFragmentManager, "CategorySheet")
            }

            clProduct.setOnClickListener {
                SelectListSheet(
                    itemsList = productsList.toMutableList(),
                    anyItemObjectIfApplicable = ProductModel("-1", anyText),
                    selectedItem = viewModel.selectedProductLiveData.value,
                    sheetTitle = getString(R.string.products),
                    sheetSubTitle = getString(R.string.please_pick_product)
                ) {
                    tvProduct.text = it?.name ?: anyText
                    viewModel.setProduct(it)
                }.show(supportFragmentManager, "ProductsSheet")
            }

            clStartsAt.setOnClickListener {
                with(Calendar.getInstance()) {
                    viewModel.startsAt.let { time = it }
                    DatePickerDialog(
                        this@CreateEditOfferActivity,
                        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                            set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                            tvStartsAt.text = time.time.getDateText("EE, d MMM yyyy")
                            viewModel.startsAt = time
                        }, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }

            clExpiresAt.setOnClickListener {
                with(Calendar.getInstance()) {
                    viewModel.expiresAt.let { time = it }
                    DatePickerDialog(
                        this@CreateEditOfferActivity,
                        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                            set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                            tvExpiresAt.text = time.time.getDateText("EE, d MMM yyyy")
                            viewModel.expiresAt = time
                        }, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }

            btnSubmit.setOnClickListener { startValidationAndPrepareData() }
        }
    }

    private fun startImagePicking() {
        ImagePicker.with(this).crop(5f, 2f).compress(1024).createIntent {
            startForImagePickingResult.launch(it)
        }
    }

    private fun initObservers() {
        with(viewModel) {
            categoriesLiveData.observe(this@CreateEditOfferActivity) { categoriesList = it }
            productsLiveData.observe(this@CreateEditOfferActivity) { productsList = it }
            selectedCategoryLiveData.observe(this@CreateEditOfferActivity) {
                binding.tvCategory.text = it?.name ?: anyText
            }
            selectedProductLiveData.observe(this@CreateEditOfferActivity) {
                binding.tvProduct.text = it?.name ?: anyText
            }
        }
    }

    private fun startValidationAndPrepareData() {
        // Image
        if (chosenImage == null && viewModel.offerModel?.imgUrl == null) {
            showErrorMsg(getString(R.string.please_pick_an_image_first))
            return
        }
        // Text
        val text = binding.etOfferText.text.toString().trim()
        if (text.isEmpty()) {
            showErrorMsg(getString(R.string.text_field_cannot_be_empty))
            return
        }

        // Claim Value
        val claimValue = try {
            binding.etOfferClaimValue.text.toString().trim()
                .convertArabicNumbersIfExist().getDigit().toFloat()
        } catch (e: Exception) {
            e.printStackTrace(); -1.0f
        }
        if (claimValue <= 0.0f) {
            showErrorMsg(getString(R.string.invalid_claim_value))
            return
        }

        // Sell Count
        val sellCount = try {
            binding.etSellCount.text.toString().trim()
                .convertArabicNumbersIfExist().getDigit().toInt()
        } catch (e: Exception) {
            e.printStackTrace(); -1
        }
        if (sellCount <= 0) {
            showErrorMsg(getString(R.string.invalid_claim_value))
            return
        }

        // Can Repeat
        val canRepeat = binding.switchCanRepeat.isChecked

        if (chosenImage != null)
            viewModel.uploadOfferImageAndGetUrl(chosenImage!!)
                .observe(this) { imageUrl ->
                    createUpdateOffer(text, imageUrl ?: "", sellCount, canRepeat, claimValue)
                }
        else
            createUpdateOffer(
                text,
                viewModel.offerModel?.imgUrl ?: "",
                sellCount,
                canRepeat,
                claimValue
            )

    }

    private fun createUpdateOffer(
        text: String,
        imageUrl: String,
        neededSellCount: Int,
        canRepeat: Boolean,
        valPerRepeat: Float,
    ) {
        viewModel.createUpdateOffer(text, imageUrl, neededSellCount, canRepeat, valPerRepeat)
            .observe(this) {
                showSuccessMsg(
                    getString(
                        if (viewModel.offerModel == null) R.string.offer_created_successfully
                        else R.string.offer_updated_successfully
                    )
                )
                finish()
            }
    }
}