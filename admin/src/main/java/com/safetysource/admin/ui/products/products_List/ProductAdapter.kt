package com.safetysource.admin.ui.products.products_List

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.core.R
import com.safetysource.core.databinding.ItemProductBinding
import com.safetysource.data.model.ProductModel
import com.squareup.picasso.Picasso

@SuppressLint("SetTextI18n")
class ProductAdapter constructor(
    private val itemWidthInPx: Int,
    private val onItemClicked: ((product: ProductModel) -> Unit)? = null,
    private val onEditClicked: ((product: ProductModel) -> Unit)? = null,
) : ListAdapter<ProductModel, ProductAdapter.ProductItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.layoutParams.width = itemWidthInPx
        return ProductItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ProductItemViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductModel) {
            with(binding) {

                // Product Image
                if (!item.imgUrl.isNullOrEmpty())
                    Picasso.get()
                        .load(item.imgUrl ?: "")
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivProductImage)
                else
                    ivProductImage.setImageURI(null)

                // Product Name
                tvProductName.text = item.name

                // Click Listeners
                root.setOnClickListener {
                    onItemClicked?.invoke(item)
                }
                ivEdit.setOnClickListener {
                    onEditClicked?.invoke(item)
                }

            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductModel>() {
            override fun areItemsTheSame(
                oldItem: ProductModel,
                newItem: ProductModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProductModel,
                newItem: ProductModel
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.imgUrl == newItem.imgUrl &&
                    oldItem.name == newItem.name &&
                    oldItem.wholesalePrice == newItem.wholesalePrice &&
                    oldItem.commissionValue == newItem.commissionValue &&
                    oldItem.updatedAt == newItem.updatedAt
        }
    }

}