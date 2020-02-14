package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.databinding.ItemRvFragmentCustomerBinding
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel

class CustomerFragmentAdapter(private val viewModel: CustomerViewModel) :
    ListAdapter<Customers, CustomerFragmentAdapter.CustomersFragmentViewHolder>(CustomerDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomersFragmentViewHolder
    {
        val binding = ItemRvFragmentCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomersFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomersFragmentViewHolder, position: Int) = holder.bind(viewModel, getItem(position))

    class CustomersFragmentViewHolder(private val binding: ItemRvFragmentCustomerBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: CustomerViewModel, customer: Customers)
        {
            binding.customer = customer
            binding.viewModel = viewModel
            binding.rvItemCustomerCardView.setOnLongClickListener {
                viewModel.onLongClickCustomer(customer.id)
                return@setOnLongClickListener true
            }
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<Customers>()
    {
        override fun areItemsTheSame(oldItem: Customers, newItem: Customers): Boolean
        {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Customers, newItem: Customers): Boolean
        {
            return oldItem == newItem
        }
    }
}