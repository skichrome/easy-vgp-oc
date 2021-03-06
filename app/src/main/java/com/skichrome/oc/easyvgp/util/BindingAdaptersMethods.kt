package com.skichrome.oc.easyvgp.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.view.fragments.adapters.*
import java.text.SimpleDateFormat

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

@BindingAdapter(value = ["items_reports"])
fun setReportItems(listView: RecyclerView, reports: List<VgpListItem>?) = reports?.let {
    (listView.adapter as VgpListFragmentAdapter).submitList(it)
}

@BindingAdapter(value = ["items_ctrl_points_vgp"])
fun setCtrlPointDataItems(listView: RecyclerView, controlPoints: List<ControlPointDataVgp>?) = controlPoints?.let {
    (listView.adapter as ControlPointNewVgpAdapter).submitList(it)
}

@BindingAdapter(value = ["items_home_report"])
fun setHomeItems(listView: RecyclerView, reports: List<HomeEndValidityReportItem>?) = reports?.let {
    (listView.adapter as HomeReportFragmentAdapter).submitList(it)
}

@BindingAdapter(value = ["bind_date"])
fun setDateFormatted(textView: TextView, dateMillis: Long) = textView.apply {
    text = getDateFormatted(dateMillis)
}

fun getDateFormatted(dateMillis: Long): String = SimpleDateFormat.getDateInstance().format(dateMillis)

@BindingAdapter(value = ["hint_and_asterisk"])
fun setHintWithAsterisk(textInputLayout: TextInputLayout, hintResource: String?) = textInputLayout.apply {
    hint = "$hintResource *"
    helperText = resources.getString(R.string.text_input_edit_text_required_help)
}