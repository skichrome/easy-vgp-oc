package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.ItemRvFragmentVgpBinding
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.viewmodel.VgpViewModel

class ControlPointNewVgpAdapter(private val viewModel: VgpViewModel) :
    ListAdapter<ControlPointDataVgp, ControlPointNewVgpAdapter.MachineTypeViewHolder>(ControlPointVgpDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineTypeViewHolder
    {
        val binding = ItemRvFragmentVgpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MachineTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MachineTypeViewHolder, position: Int) =
        holder.bind(controlPoint = getItem(position), viewModel = viewModel, position = position)

    class MachineTypeViewHolder(private val binding: ItemRvFragmentVgpBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(controlPoint: ControlPointDataVgp, position: Int, viewModel: VgpViewModel)
        {
            binding.ctrlPoint = controlPoint
            binding.viewModel = viewModel

            binding.rvItemFragVgpCtrlPointComment.setOnClickListener { viewModel.onClickCommentEvent(position) }

            binding.rvItemFragVgpCtrlPointPossibilityGoodState.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                {
                    controlPoint.choicePossibilityId = 1
                    binding.rvItemFragVgpCtrlPointStateViewIndicator.setBackgroundResource(R.color.ctrlPointChoiceBE)
                }
            }
            binding.rvItemFragVgpCtrlPointPossibilityMediumState.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                {
                    controlPoint.choicePossibilityId = 2
                    binding.rvItemFragVgpCtrlPointStateViewIndicator.setBackgroundResource(R.color.ctrlPointChoiceEM)
                }
            }
            binding.rvItemFragVgpCtrlPointPossibilityBadState.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                {
                    controlPoint.choicePossibilityId = 3
                    binding.rvItemFragVgpCtrlPointStateViewIndicator.setBackgroundResource(R.color.ctrlPointChoiceME)
                }
            }
        }
    }

    class ControlPointVgpDiffCallback : DiffUtil.ItemCallback<ControlPointDataVgp>()
    {
        override fun areItemsTheSame(oldItem: ControlPointDataVgp, newItem: ControlPointDataVgp): Boolean =
            oldItem.controlPoint.id == newItem.controlPoint.id

        override fun areContentsTheSame(oldItem: ControlPointDataVgp, newItem: ControlPointDataVgp): Boolean = oldItem == newItem
    }
}