package com.safetysource.core.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.core.databinding.ItemSelectListBinding
import com.safetysource.data.model.Filterable

class SelectListAdapter constructor(
    private val selectedItemModels: List<Filterable>? = null,
    private val onSelect: (Filterable) -> Unit,
) : ListAdapter<Filterable, SelectListAdapter.ItemHolder>(Differentiator) {

    private var unfilteredList: List<Filterable>? = null

    fun modifyList(list: List<Filterable>) {
        unfilteredList = list
        submitList(list)
    }

    fun filter(query: CharSequence?) {
        if (unfilteredList == null)
            return

        val list = mutableListOf<Filterable>()

        if (!query.isNullOrEmpty()) {
            list.addAll(unfilteredList!!.filter {
                it.getFilterableName()?.lowercase()?.contains(query.toString().lowercase()) == true
            })
        } else {
            list.addAll(unfilteredList!!)
        }

        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding =
            ItemSelectListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = getItem(position)!!

        holder.binding.tvName.text = item.getFilterableName()
        holder.binding.ivCheck.visibility =
            if (selectedItemModels?.find { item.getFilterableId() == it.getFilterableId() } != null) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { onSelect.invoke(item) }
    }

    class ItemHolder(val binding: ItemSelectListBinding) : RecyclerView.ViewHolder(binding.root)

    object Differentiator : DiffUtil.ItemCallback<Filterable>() {

        override fun areItemsTheSame(oldItem: Filterable, newItem: Filterable): Boolean {
            return oldItem.getFilterableId() == newItem.getFilterableId()
        }

        override fun areContentsTheSame(
            oldItem: Filterable,
            newItem: Filterable
        ): Boolean {
            return oldItem.getFilterableId() == newItem.getFilterableId()
                    && oldItem.getFilterableName() == newItem.getFilterableName()
        }
    }
}