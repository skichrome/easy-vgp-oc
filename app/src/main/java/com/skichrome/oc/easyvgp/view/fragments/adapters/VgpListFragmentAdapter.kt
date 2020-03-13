package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.ItemRvVgpListBinding
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.setHolderBottomMargin
import com.skichrome.oc.easyvgp.viewmodel.VgpListViewModel

class VgpListFragmentAdapter(private val viewModel: VgpListViewModel) :
    ListAdapter<VgpListItem, VgpListFragmentAdapter.VgpListFragmentViewHolder>(VgpListFragmentCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VgpListFragmentViewHolder
    {
        val binding = ItemRvVgpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VgpListFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VgpListFragmentViewHolder, position: Int)
    {
        holder.setHolderBottomMargin(position == currentList.lastIndex)
        holder.bind(getItem(position), viewModel)
    }

    class VgpListFragmentViewHolder(private val binding: ItemRvVgpListBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(report: VgpListItem, viewModel: VgpListViewModel)
        {
            binding.viewModel = viewModel
            binding.report = report

            val color = if (report.isValid) R.color.reportFinal else R.color.reportDraft
            binding.rvItemFragVgpCtrlPointStateViewIndicator.setBackgroundResource(color)
        }
    }

    class VgpListFragmentCallback : DiffUtil.ItemCallback<VgpListItem>()
    {
        override fun areItemsTheSame(oldItem: VgpListItem, newItem: VgpListItem): Boolean = oldItem.controlPointDataId == newItem.controlPointDataId
        override fun areContentsTheSame(oldItem: VgpListItem, newItem: VgpListItem): Boolean = oldItem == newItem
    }
}