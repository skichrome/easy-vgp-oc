package com.skichrome.oc.easyvgp.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.view.fragments.adapters.CustomerFragmentAdapter
import com.skichrome.oc.easyvgp.view.fragments.adapters.MachineFragmentAdapter

@BindingAdapter(value = ["items_customers"])
fun setCustomerItems(listView: RecyclerView, customers: List<Customer>?) = customers?.let {
    (listView.adapter as CustomerFragmentAdapter).submitList(customers)
}

@BindingAdapter(value = ["items_machines"])
fun setMachinesItem(listView: RecyclerView, machines: List<Machine>?) = machines?.let {
    (listView.adapter as MachineFragmentAdapter).submitList(machines)
}