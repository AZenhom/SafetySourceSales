package com.safetysource.core.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.core.R
import com.safetysource.core.databinding.ItemOfferSerialBinding
import com.safetysource.data.model.OfferSerial
import com.safetysource.data.model.TransactionType

class OfferSerialsAdapter constructor(
    private val onItemClicked: ((offerSerial: OfferSerial) -> Unit)? = null,
) : ListAdapter<OfferSerial, OfferSerialsAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemOfferSerialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemOfferSerialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OfferSerial) {
            with(binding) {
                // Serial
                tvSerial.text = item.serial

                // TransactionType
                ivTransactionType.setImageResource(
                    when (item.transactionType) {
                        TransactionType.SELLING -> R.drawable.ic_arrow_up
                        TransactionType.UNSELLING -> R.drawable.ic_arrow_down
                        else -> R.drawable.ic_question_mark
                    }
                )

                // Click Listener
                rootView.setOnClickListener {
                    onItemClicked?.invoke(item)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OfferSerial>() {
            override fun areItemsTheSame(
                oldItem: OfferSerial,
                newItem: OfferSerial
            ): Boolean =
                oldItem.transactionId == newItem.transactionId

            override fun areContentsTheSame(
                oldItem: OfferSerial,
                newItem: OfferSerial
            ): Boolean = oldItem.transactionId == newItem.transactionId &&
                    oldItem.serial == newItem.serial &&
                    oldItem.transactionType == newItem.transactionType
        }
    }

}