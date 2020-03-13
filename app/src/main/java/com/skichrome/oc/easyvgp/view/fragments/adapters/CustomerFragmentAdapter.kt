package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.databinding.ItemRvFragmentCustomerBinding
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.util.setHolderBottomMargin
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel

class CustomerFragmentAdapter(private val viewModel: CustomerViewModel) :
    ListAdapter<Customer, CustomerFragmentAdapter.CustomersFragmentViewHolder>(CustomerDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomersFragmentViewHolder
    {
        val binding = ItemRvFragmentCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomersFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomersFragmentViewHolder, position: Int)
    {
        holder.setHolderBottomMargin(position == currentList.lastIndex)
        holder.bind(viewModel, getItem(position))
    }

    class CustomersFragmentViewHolder(private val binding: ItemRvFragmentCustomerBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: CustomerViewModel, customer: Customer)
        {
            binding.customer = customer
            binding.viewModel = viewModel
            binding.rvItemCustomerCardView.setOnLongClickListener {
                viewModel.onLongClickCustomer(customer.id)
                return@setOnLongClickListener true
            }
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<Customer>()
    {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean = oldItem == newItem
    }
}