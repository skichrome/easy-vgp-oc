package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.databinding.ItemRvMachineTypeBinding
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.viewmodel.AdminViewModel

class MachineTypeAdapter(private val viewModel: AdminViewModel) :
    ListAdapter<MachineType, MachineTypeAdapter.MachineTypeViewHolder>(MachineTypeDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineTypeViewHolder
    {
        val binding = ItemRvMachineTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MachineTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MachineTypeViewHolder, position: Int) = holder.bind(machineType = getItem(position), viewModel = viewModel)

    class MachineTypeViewHolder(private val binding: ItemRvMachineTypeBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(machineType: MachineType, viewModel: AdminViewModel)
        {
            binding.machineType = machineType
            binding.viewModel = viewModel
            binding.rvItemMachineTypeRootFrameLayout.setOnLongClickListener {
                viewModel.onLongClickMachineType(machineType)
                return@setOnLongClickListener true
            }
        }
    }

    class MachineTypeDiffCallback : DiffUtil.ItemCallback<MachineType>()
    {
        override fun areItemsTheSame(oldItem: MachineType, newItem: MachineType): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MachineType, newItem: MachineType): Boolean = oldItem == newItem
    }
}