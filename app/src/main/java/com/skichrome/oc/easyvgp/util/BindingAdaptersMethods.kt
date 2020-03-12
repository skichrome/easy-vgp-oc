package com.skichrome.oc.easyvgp.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.view.fragments.adapters.*

@BindingAdapter(value = ["items_customers"])
fun setCustomerItems(listView: RecyclerView, customers: List<Customer>?) = customers?.let {
    (listView.adapter as CustomerFragmentAdapter).submitList(customers)
}

@BindingAdapter(value = ["items_machines"])
fun setMachinesItem(listView: RecyclerView, machines: List<Machine>?) = machines?.let {
    (listView.adapter as MachineFragmentAdapter).submitList(machines)
}

@BindingAdapter(value = ["items_machine_types"])
fun setMachineTypesItems(listView: RecyclerView, machineTypes: List<MachineType>?) = machineTypes?.let {
    (listView.adapter as MachineTypeAdapter).submitList(machineTypes)
}

@BindingAdapter(value = ["items_ctrl_points"])
fun setControlPointsItems(listView: RecyclerView, controlPoints: List<ControlPoint>?) = controlPoints?.let {
    (listView.adapter as ControlPointAdapter).submitList(it)
}

@BindingAdapter(value = ["items_ctrl_points_vgp"])
fun setCtrlPointDataItems(listView: RecyclerView, controlPoints: List<ControlPoint>?) = controlPoints?.let {
    (listView.adapter as ControlPointVgpAdapter).submitList(it)
}