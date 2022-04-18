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
import com.safetysource.core.ui.adapters.SelectItemModel
import com.safetysource.core.ui.adapters.SelectListAdapter

class SelectListSheet constructor(
    private val itemsList: List<SelectItemModel>,
    private val selectedItem: SelectItemModel? = null,
    private val onSelect: (SelectItemModel?) -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetSelectListBinding

    private lateinit var adapter: SelectListAdapter

    init {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SheetSelectListBinding.inflate(inflater, container, false)

        adapter = SelectListAdapter(selectedItem) {
            onSelect.invoke(it)
            dismiss()
        }
        binding.rvBranches.adapter = adapter
        adapter.submitList(itemsList)

        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.rvBranches.addItemDecoration(decoration)

        return binding.root
    }
}