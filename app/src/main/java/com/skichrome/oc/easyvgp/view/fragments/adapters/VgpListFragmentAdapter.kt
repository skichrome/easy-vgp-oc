package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.databinding.ItemRvVgpListBinding
import com.skichrome.oc.easyvgp.viewmodel.VgpListViewModel

class VgpListFragmentAdapter(private val viewModel: VgpListViewModel) :
    ListAdapter<Long, VgpListFragmentAdapter.VgpListFragmentViewHolder>(VgpListFragmentCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VgpListFragmentViewHolder
    {
        val binding = ItemRvVgpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VgpListFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VgpListFragmentViewHolder, position: Int) = holder.bind(getItem(position), viewModel)

    class VgpListFragmentViewHolder(private val binding: ItemRvVgpListBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(report: Long, viewModel: VgpListViewModel)
        {
            binding.viewModel = viewModel
            binding.report = report
        }
    }

    class VgpListFragmentCallback : DiffUtil.ItemCallback<Long>()
    {
        override fun areItemsTheSame(oldItem: Long, newItem: Long): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Long, newItem: Long): Boolean = oldItem == newItem
    }
}