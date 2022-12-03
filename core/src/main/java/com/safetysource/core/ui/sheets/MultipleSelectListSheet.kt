package com.safetysource.core.ui.sheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.safetysource.core.R
import com.safetysource.core.databinding.SheetSelectListBinding
import com.safetysource.core.ui.adapters.SelectListAdapter
import com.safetysource.core.ui.makeVisible
import com.safetysource.data.model.Filterable

class MultipleSelectListSheet<T : Filterable> constructor(
    private val itemsList: MutableList<T> = mutableListOf(),
    private val selectedItems: MutableList<T> = mutableListOf(),
    private val sheetTitle: String? = null,
    private val sheetSubTitle: String? = null,
    private val onSelect: (MutableList<T?>) -> Unit,
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "MultipleSelectListSheet"
    }

    private lateinit var binding: SheetSelectListBinding

    private lateinit var adapter: SelectListAdapter

    init {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SheetSelectListBinding.inflate(inflater, container, false)

        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        adapter = SelectListAdapter(selectedItems) {
            if (selectedItems.contains(it))
                selectedItems.remove(it)
            else
                selectedItems.add(it as T)
            adapter.notifyDataSetChanged()
        }
        adapter.submitList(itemsList as List<Filterable>)

        with(binding) {
            rvItems.adapter = adapter
            rvItems.addItemDecoration(decoration)
            tvSheetTitle.text = sheetTitle
            tvSheetSubTitle.text = sheetSubTitle
            btnSubmit.makeVisible()
            btnSubmit.setOnClickListener {
                onSelect.invoke(selectedItems.toMutableList())
                dismiss()
            }
            return root
        }
    }
}