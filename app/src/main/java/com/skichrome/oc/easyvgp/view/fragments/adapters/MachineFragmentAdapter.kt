package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.databinding.ItemRvFragmentMachineBinding
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.util.loadPhotoWithGlide
import com.skichrome.oc.easyvgp.viewmodel.MachineViewModel

class MachineFragmentAdapter(private val viewModel: MachineViewModel) :
    ListAdapter<Machine, MachineFragmentAdapter.CustomersFragmentViewHolder>(MachineDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomersFragmentViewHolder
    {
        val binding = ItemRvFragmentMachineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomersFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomersFragmentViewHolder, position: Int) =
        holder.bind(viewModel, getItem(position))

    class CustomersFragmentViewHolder(private val binding: ItemRvFragmentMachineBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: MachineViewModel, machine: Machine)
        {
            binding.machine = machine
            binding.viewModel = viewModel

            machine.remotePhotoRef?.let { binding.rvItemFragmentMachineImage.loadPhotoWithGlide(it) }
                ?: machine.localPhotoRef?.let { binding.rvItemFragmentMachineImage.loadPhotoWithGlide(it) }
        }
    }

    class MachineDiffCallback : DiffUtil.ItemCallback<Machine>()
    {
        override fun areItemsTheSame(oldItem: Machine, newItem: Machine): Boolean = oldItem.machineId == newItem.machineId
        override fun areContentsTheSame(oldItem: Machine, newItem: Machine): Boolean = oldItem == newItem
    }
}