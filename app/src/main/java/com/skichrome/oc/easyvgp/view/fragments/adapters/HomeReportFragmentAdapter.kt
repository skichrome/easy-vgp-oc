package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.ItemRvHomeFragmentBinding
import com.skichrome.oc.easyvgp.model.local.database.HomeEndValidityReportItem
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel
import java.io.File

class HomeReportFragmentAdapter(private val viewModel: HomeViewModel) :
    ListAdapter<HomeEndValidityReportItem, HomeReportFragmentAdapter.HomeReportViewHolder>(CustomerDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeReportViewHolder
    {
        val binding = ItemRvHomeFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeReportViewHolder, position: Int) =
        holder.bind(viewModel, getItem(position))

    class HomeReportViewHolder(private val binding: ItemRvHomeFragmentBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: HomeViewModel, report: HomeEndValidityReportItem)
        {
            binding.report = report
            binding.viewModel = viewModel

            report.remotePicture?.let { remotePhoto ->
                Glide.with(binding.root).load(remotePhoto).centerCrop().into(binding.itemRvHomeFragmentMachinePicture)
            }
                ?: report.localPicture?.let { localPhoto ->
                    Glide.with(binding.root).load(File(localPhoto)).centerCrop().into(binding.itemRvHomeFragmentMachinePicture)
                }
                ?: Glide.with(binding.root).load(R.drawable.easy_vgp_icon).into(binding.itemRvHomeFragmentMachinePicture)

            binding.executePendingBindings()
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<HomeEndValidityReportItem>()
    {
        override fun areItemsTheSame(oldItem: HomeEndValidityReportItem, newItem: HomeEndValidityReportItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: HomeEndValidityReportItem, newItem: HomeEndValidityReportItem): Boolean = oldItem == newItem
    }
}