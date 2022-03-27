package com.safetysource.core.ui

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.safetysource.core.R
import com.safetysource.core.databinding.DialogInfoLayoutBinding
import kotlin.math.roundToInt

class InfoDialog constructor(
    context: Context,
    private val message: String,
    private val confirmText: String? = context.getString(R.string.confirm),
    private val cancelText: String? = context.getString(R.string.cancel),
    private val imageRes: Int? = null,
    private val onConfirm: (() -> Unit)? = null,
    private val onCancel: (() -> Unit)? = null,
    private val isCancelable: Boolean? = false,
) : DialogFragment() {

    companion object {
        const val TAG = "InfoDialog"
    }

    private var _binding: DialogInfoLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppDialogStyle)
        super.setCancelable(isCancelable ?: false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogInfoLayoutBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        with(binding) {
            if (imageRes == null) {
                ivImage.visibility = View.GONE

                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 0)
                tvMessage.layoutParams = params

                tvMessage.setPadding(
                    convertDpToPixel(16f),
                    convertDpToPixel(24f),
                    convertDpToPixel(16f),
                    0
                )
            } else {
                ivImage.setImageResource(imageRes)
            }

            tvMessage.text = message

            if (confirmText == null)
                btnConfirm.visibility = View.GONE
            else
                btnConfirm.text = confirmText

            if (cancelText == null)
                btnCancel.visibility = View.GONE
            else
                btnCancel.text = cancelText

            btnConfirm.setOnClickListener {
                onConfirm?.invoke()
            }

            btnCancel.setOnClickListener {
                if (onCancel == null)
                    dismiss()
                else
                    onCancel.invoke()
            }
        }
    }

    fun convertDpToPixel(dp: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return px.roundToInt()
    }
}