package com.safetysource.core.ui.sheets

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
import com.safetysource.data.model.Filterable

class SelectListSheet<T : Filterable> constructor(
    private val anyItemObjectIfApplicable: T? = null, // Used if the list logic contains a neutral (any) option
    private val itemsList: MutableList<T> = mutableListOf(),
    private val selectedItem: T? = null,
    private val sheetTitle: String? = null,
    private val sheetSubTitle: String? = null,
    private val onSelect: (T?) -> Unit,
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "SelectListSheet"
    }

    private lateinit var binding: SheetSelectListBinding

    private lateinit var adapter: SelectListAdapter

    init {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)

        anyItemObjectIfApplicable?.let { itemsList.add(0, it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SheetSelectListBinding.inflate(inflater, container, false)

        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        val preSelectedItems = if (selectedItem == null) emptyList() else listOf(selectedItem)
        adapter = SelectListAdapter(preSelectedItems) {
            onSelect.invoke(if (it == anyItemObjectIfApplicable) null else it as T)
            dismiss()
        }
        adapter.submitList(itemsList as List<Filterable>?)

        with(binding) {
            rvItems.adapter = adapter
            rvItems.addItemDecoration(decoration)
            tvSheetTitle.text = sheetTitle
            tvSheetSubTitle.text = sheetSubTitle
            return root
        }
    }
}