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
    private var itemsList: MutableList<T> = mutableListOf(),
    private var selectedItem: T? = null,
    private val onSelect: (T?) -> Unit,
) : BottomSheetDialogFragment() {

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

        adapter = SelectListAdapter(selectedItem) {
            selectedItem = if (it == anyItemObjectIfApplicable) null else it as T
            onSelect.invoke(selectedItem)
            dismiss()
        }
        binding.rvBranches.adapter = adapter
        adapter.submitList(itemsList as List<Filterable>?)

        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.rvBranches.addItemDecoration(decoration)

        return binding.root
    }
}