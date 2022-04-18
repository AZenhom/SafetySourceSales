package com.safetysource.core.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.core.databinding.ItemSelectListBinding

class SelectListAdapter constructor(
    private val selectedItemModel: SelectItemModel? = null,
    private val onSelect: (SelectItemModel) -> Unit,
) : ListAdapter<SelectItemModel, SelectListAdapter.ItemHolder>(Differentiator) {

    private var unfilteredList: List<SelectItemModel>? = null

    fun modifyList(list: List<SelectItemModel>) {
        unfilteredList = list
        submitList(list)
    }

    fun filter(query: CharSequence?) {
        if (unfilteredList == null)
            return

        val list = mutableListOf<SelectItemModel>()

        if (!query.isNullOrEmpty()) {
            list.addAll(unfilteredList!!.filter {
                it.name.lowercase().contains(query.toString().lowercase())
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

        holder.binding.tvName.text = item.name
        holder.binding.ivCheck.visibility =
            if (item.id == selectedItemModel?.id) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { onSelect.invoke(item) }
    }

    class ItemHolder(val binding: ItemSelectListBinding) : RecyclerView.ViewHolder(binding.root)

    object Differentiator : DiffUtil.ItemCallback<SelectItemModel>() {

        override fun areItemsTheSame(oldItem: SelectItemModel, newItem: SelectItemModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SelectItemModel,
            newItem: SelectItemModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}

data class SelectItemModel(val id: String, val name: String)