package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.ItemRvFragmentVgpBinding
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.viewmodel.VgpViewModel

class ControlPointVgpAdapter(private val viewModel: VgpViewModel) :
    ListAdapter<ControlPoint, ControlPointVgpAdapter.MachineTypeViewHolder>(ControlPointAdapter.ControlPointDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineTypeViewHolder
    {
        val binding = ItemRvFragmentVgpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MachineTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MachineTypeViewHolder, position: Int) = holder.bind(controlPoint = getItem(position), viewModel = viewModel)

    class MachineTypeViewHolder(private val binding: ItemRvFragmentVgpBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(controlPoint: ControlPoint, viewModel: VgpViewModel)
        {
            binding.ctrlPoint = controlPoint
            binding.viewModel = viewModel

            binding.rvItemFragVgpCtrlPointPossibilityBadState.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    binding.rvItemFragVgpCtrlPointStateViewIndicator.setBackgroundResource(R.color.ctrlPointChoiceME)
            }
            binding.rvItemFragVgpCtrlPointPossibilityMediumState.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    binding.rvItemFragVgpCtrlPointStateViewIndicator.setBackgroundResource(R.color.ctrlPointChoiceEM)
            }
            binding.rvItemFragVgpCtrlPointPossibilityGoodState.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    binding.rvItemFragVgpCtrlPointStateViewIndicator.setBackgroundResource(R.color.ctrlPointChoiceBE)
            }
        }
    }
}