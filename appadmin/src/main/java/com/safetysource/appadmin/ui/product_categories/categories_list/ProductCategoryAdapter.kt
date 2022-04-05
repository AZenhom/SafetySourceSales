package com.safetysource.appadmin.ui.product_categories.categories_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.core.R
import com.safetysource.core.databinding.ItemProductCategroyBinding
import com.safetysource.data.model.ProductCategoryModel
import com.squareup.picasso.Picasso

class ProductCategoryAdapter constructor(
    private val onItemClicked: ((category: ProductCategoryModel) -> Unit)? = null,
    private val onEditClicked: ((category: ProductCategoryModel) -> Unit)? = null,
) :
    ListAdapter<ProductCategoryModel, ProductCategoryAdapter.ProductCategoryItemViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductCategoryItemViewHolder {
        val binding =
            ItemProductCategroyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductCategoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductCategoryItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ProductCategoryItemViewHolder(private val binding: ItemProductCategroyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductCategoryModel) {
            with(binding) {
                Picasso.get()
                    .load(item.imgUrl)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivCategory)

                tvCategoryName.text = item.name

                clBackground.setOnClickListener {
                    onItemClicked?.invoke(item)
                }

                ivEdit.setOnClickListener {
                    onEditClicked?.invoke(item)
                }

            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductCategoryModel>() {
            override fun areItemsTheSame(
                oldItem: ProductCategoryModel,
                newItem: ProductCategoryModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProductCategoryModel,
                newItem: ProductCategoryModel
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.imgUrl == newItem.imgUrl &&
                    oldItem.rank == newItem.rank &&
                    oldItem.updatedAt == newItem.updatedAt
        }
    }

}