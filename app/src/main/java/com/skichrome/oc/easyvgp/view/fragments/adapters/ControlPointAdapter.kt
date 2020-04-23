package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.databinding.ItemRvCtrlPointsBinding
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.util.setHolderBottomMargin
import com.skichrome.oc.easyvgp.viewmodel.AdminViewModel

class ControlPointAdapter(private val viewModel: AdminViewModel) :
    ListAdapter<ControlPoint, ControlPointAdapter.MachineTypeViewHolder>(ControlPointDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineTypeViewHolder
    {
        val binding = ItemRvCtrlPointsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MachineTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MachineTypeViewHolder, position: Int)
    {
        holder.setHolderBottomMargin(position == currentList.lastIndex)
        holder.bind(controlPoint = getItem(position), viewModel = viewModel)
    }

    class MachineTypeViewHolder(private val binding: ItemRvCtrlPointsBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(controlPoint: ControlPoint, viewModel: AdminViewModel)
        {
            binding.controlPoint = controlPoint
            binding.viewModel = viewModel
        }
    }

    class ControlPointDiffCallback : DiffUtil.ItemCallback<ControlPoint>()
    {
        override fun areItemsTheSame(oldItem: ControlPoint, newItem: ControlPoint): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ControlPoint, newItem: ControlPoint): Boolean = oldItem == newItem
    }
}