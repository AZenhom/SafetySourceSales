package com.safetysource.admin.ui.retailers.retailers_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.admin.databinding.ItemRetailerBinding
import com.safetysource.core.ui.setIsVisible
import com.safetysource.data.model.RetailerModel

class RetailersAdapter constructor(
    private val onItemClicked: ((transaction: RetailerModel) -> Unit)? = null,
    private val onEditClicked: ((transaction: RetailerModel) -> Unit)? = null,
    private val onRemoveClicked: ((transaction: RetailerModel) -> Unit)? = null,
) : ListAdapter<RetailerModel, RetailersAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemRetailerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemRetailerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RetailerModel) {
            with(binding) {
                // Retailer Name
                tvName.text = item.name

                // Retailer Contact No
                tvPhoneNo.text = item.contactNo

                // Remove Retailer from Team
                ivRemove.setIsVisible(onRemoveClicked != null)

                // Click Listeners
                ivEdit.setOnClickListener {
                    onEditClicked?.invoke(item)
                }
                ivRemove.setOnClickListener {
                    onRemoveClicked?.invoke(item)
                }
                clRootView.setOnClickListener {
                    onItemClicked?.invoke(item)
                }

            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RetailerModel>() {
            override fun areItemsTheSame(
                oldItem: RetailerModel,
                newItem: RetailerModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: RetailerModel,
                newItem: RetailerModel
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.name == newItem.name
        }
    }

}