package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.databinding.ItemRvFragmentMachineBinding
import com.skichrome.oc.easyvgp.model.local.database.Machines
import com.skichrome.oc.easyvgp.viewmodel.MachineViewModel

class MachineFragmentAdapter(private val viewModel: MachineViewModel) :
    ListAdapter<Machines, MachineFragmentAdapter.CustomersFragmentViewHolder>(MachineDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomersFragmentViewHolder
    {
        val binding = ItemRvFragmentMachineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomersFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomersFragmentViewHolder, position: Int) = holder.bind(viewModel, getItem(position))

    class CustomersFragmentViewHolder(private val binding: ItemRvFragmentMachineBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: MachineViewModel, machine: Machines)
        {
            binding.machine = machine
            binding.viewModel = viewModel
            binding.rvItemMachineCardView.setOnLongClickListener {
                viewModel.onLongClickMachine(machine.machineId)
                return@setOnLongClickListener true
            }
        }
    }

    class MachineDiffCallback : DiffUtil.ItemCallback<Machines>()
    {
        override fun areItemsTheSame(oldItem: Machines, newItem: Machines): Boolean
        {
            return oldItem.machineId == newItem.machineId
        }

        override fun areContentsTheSame(oldItem: Machines, newItem: Machines): Boolean
        {
            return oldItem == newItem
        }
    }
}