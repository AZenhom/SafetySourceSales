package com.safetysource.appadmin.ui.teams

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safetysource.appadmin.databinding.ItemTeamBinding
import com.safetysource.data.model.TeamModel

class TeamsAdapter constructor(
    private val onItemClicked: ((transaction: TeamModel) -> Unit)? = null,
    private val onEditClicked: ((transaction: TeamModel) -> Unit)? = null,
) : ListAdapter<TeamModel, TeamsAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TeamModel) {
            with(binding) {
                // Team Data
                tvTeamName.text = item.name
                tvDueCommission.text = "${item.teamReportModel?.dueCommissionValue} EGP"
                tvTotalRedeemed.text = "${item.teamReportModel?.totalRedeemed} EGP"

                // Click Listeners
                ivEdit.setOnClickListener {
                    onEditClicked?.invoke(item)
                }
                clRootView.setOnClickListener {
                    onItemClicked?.invoke(item)
                }

            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TeamModel>() {
            override fun areItemsTheSame(
                oldItem: TeamModel,
                newItem: TeamModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TeamModel,
                newItem: TeamModel
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.name == newItem.name
        }
    }

}