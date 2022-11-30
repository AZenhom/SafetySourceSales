package com.safetysource.core.ui.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.safetysource.core.R
import com.safetysource.core.databinding.SheetOfferSerialsBinding
import com.safetysource.core.ui.adapters.OfferSerialsAdapter
import com.safetysource.data.model.OfferSerial

class OfferSerialsSheet constructor(
    private val offerSerials: List<OfferSerial> = listOf(),
    private val onSelect: (OfferSerial) -> Unit,
) : BottomSheetDialogFragment() {

    init {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SheetOfferSerialsBinding.inflate(inflater, container, false)

        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        val adapter = OfferSerialsAdapter {
            onSelect.invoke(it)
            dismiss()
        }
        adapter.submitList(offerSerials)

        with(binding) {
            rvItems.adapter = adapter
            rvItems.addItemDecoration(decoration)
            return root
        }
    }
}