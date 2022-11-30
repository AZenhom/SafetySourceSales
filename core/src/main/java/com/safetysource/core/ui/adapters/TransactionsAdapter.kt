package com.safetysource.core.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.core.R
import com.safetysource.core.databinding.ItemTransactionBinding
import com.safetysource.core.utils.getDateText
import com.safetysource.data.model.OfferSerial
import com.safetysource.data.model.TransactionModel
import com.safetysource.data.model.TransactionType

@SuppressLint("SetTextI18n")
class TransactionsAdapter constructor(
    private val onItemClicked: ((transaction: TransactionModel) -> Unit)? = null,
    private val onMultipleSerialsClicked: ((serials: List<OfferSerial>) -> Unit)? = null,
) : ListAdapter<TransactionModel, TransactionsAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionModel) {
            with(binding) {
                // Retailer
                tvRetailerName.text = item.retailerModel?.name

                // Team
                tvTeamName.text = item.teamModel?.name

                // Product
                tvProductName.text = item.productModel?.name

                // Serial
                if (!item.offerSerials.isNullOrEmpty()) {
                    tvSerial.text = tvSerial.context.getString(R.string.click_to_view_serials)
                    tvSerial.setOnClickListener { onMultipleSerialsClicked?.invoke(item.offerSerials!!) }
                } else {
                    tvSerial.text = item.serial
                    tvSerial.setOnClickListener(null)
                }

                // Admin Action Required
                tvAdminActionRequired.text =
                    tvAdminActionRequired.context.getString(
                        if (item.type == TransactionType.UNSELLING && item.isUnsellingApproved == false) R.string.yes
                        else R.string.no
                    )

                // Commission or Offer
                lblCommission.text = lblCommission.context.getString(
                    when (item.type) {
                        TransactionType.SELLING -> R.string.commission_applied
                        TransactionType.UNSELLING -> R.string.commission_removed
                        TransactionType.OFFER_CLAIM -> R.string.offer_claimed
                        else -> R.string.empty
                    }
                )
                tvCommission.text =
                    "${item.commissionAppliedOrRemoved} ${tvCommission.context.getString(R.string.egyptian_pound)}"

                // Transaction Type Image
                ivTransactionType.setImageResource(
                    when (item.type) {
                        TransactionType.SELLING -> R.drawable.ic_arrow_up
                        TransactionType.UNSELLING -> R.drawable.ic_arrow_down
                        TransactionType.OFFER_CLAIM -> R.drawable.ic_offer
                        else -> R.drawable.ic_question_mark
                    }
                )

                // Last Updated
                tvLastUpdated.text = item.updatedAt?.time?.getDateText("EE, d MMM yyyy, hh:mm aa")

                // Click Listeners
                rootView.setOnClickListener {
                    onItemClicked?.invoke(item)
                }

            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionModel>() {
            override fun areItemsTheSame(
                oldItem: TransactionModel,
                newItem: TransactionModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TransactionModel,
                newItem: TransactionModel
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.type == newItem.type &&
                    oldItem.isUnsellingApproved == newItem.isUnsellingApproved &&
                    oldItem.updatedAt == newItem.updatedAt
        }
    }

}