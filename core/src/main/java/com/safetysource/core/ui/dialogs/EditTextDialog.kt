package com.safetysource.core.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.safetysource.core.R
import com.safetysource.core.databinding.DialogEditTextLayoutBinding

class EditTextDialog constructor(
    context: Context,
    private val message: String,
    private val confirmText: String? = context.getString(R.string.confirm),
    private val cancelText: String? = context.getString(R.string.cancel),
    private val editTextHint: String? = null,
    private val onConfirm: ((text: String) -> Unit)? = null,
    private val onCancel: (() -> Unit)? = null,
    private val isCancelable: Boolean? = false,
) : DialogFragment() {

    companion object {
        const val TAG = "EditTextDialog"
    }

    private var _binding: DialogEditTextLayoutBinding? = null
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
        _binding = DialogEditTextLayoutBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        with(binding) {
            tvMessage.text = message

            etText.hint = editTextHint

            if (confirmText == null)
                btnConfirm.visibility = View.GONE
            else
                btnConfirm.text = confirmText

            if (cancelText == null)
                btnCancel.visibility = View.GONE
            else
                btnCancel.text = cancelText

            btnConfirm.setOnClickListener {
                val text = etText.text.toString().trim()
                if (text.isNotEmpty())
                    onConfirm?.invoke(text)
            }

            btnCancel.setOnClickListener {
                if (onCancel == null)
                    dismiss()
                else
                    onCancel.invoke()
            }
        }
    }
}