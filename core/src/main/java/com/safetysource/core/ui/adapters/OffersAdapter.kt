package com.safetysource.core.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.core.R
import com.safetysource.core.databinding.ItemOfferBinding
import com.safetysource.core.ui.setIsVisible
import com.safetysource.core.utils.getDateText
import com.safetysource.data.model.OfferModel
import com.squareup.picasso.Picasso

@SuppressLint("SetTextI18n")
class OffersAdapter constructor(
    private val onItemClicked: ((offerModel: OfferModel) -> Unit)? = null,
    private val onEditClicked: ((offerModel: OfferModel) -> Unit)? = null,
    private val onDeleteClicked: ((offerModel: OfferModel) -> Unit)? = null,
) : ListAdapter<OfferModel, OffersAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemOfferBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OfferModel) {
            with(binding) {
                // Retailer
                Picasso.get()
                    .load(item.imgUrl)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivOffer)

                // Team
                tvOfferText.text = item.text

                // Start-Expiry Dates
                val startDate = item.startsAt?.time?.getDateText("dd-MM-yyyy")
                val expiryDate = item.expiresAt?.time?.getDateText("dd-MM-yyyy")
                tvDate.text = "$startDate ${tvDate.context.getString(R.string.to)} $expiryDate"

                // Edit & Delete Icon
                ivEdit.setIsVisible(onEditClicked != null)
                ivDelete.setIsVisible(onDeleteClicked != null)

                // Click Listeners
                rootView.setOnClickListener {
                    onItemClicked?.invoke(item)
                }
                ivEdit.setOnClickListener {
                    onEditClicked?.invoke(item)
                }
                ivDelete.setOnClickListener {
                    onDeleteClicked?.invoke(item)
                }

            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OfferModel>() {
            override fun areItemsTheSame(
                oldItem: OfferModel,
                newItem: OfferModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: OfferModel,
                newItem: OfferModel
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.imgUrl == newItem.imgUrl &&
                    oldItem.text == newItem.text &&
                    oldItem.productCategoryId == newItem.productCategoryId &&
                    oldItem.productId == newItem.productId &&
                    oldItem.neededSellCount == newItem.neededSellCount &&
                    oldItem.canRepeat == newItem.canRepeat &&
                    oldItem.valPerRepeat == newItem.valPerRepeat &&
                    oldItem.startsAt == newItem.startsAt &&
                    oldItem.expiresAt == newItem.expiresAt &&
                    oldItem.updatedAt == newItem.updatedAt
        }
    }

}