package com.skichrome.oc.easyvgp.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.view.fragments.adapters.CustomersFragmentAdapter

@BindingAdapter(value = ["items_customers"])
fun setCustomerItems(listView: RecyclerView, customers: List<Customers>?) = customers?.let {
    (listView.adapter as CustomersFragmentAdapter).submitList(customers)
}