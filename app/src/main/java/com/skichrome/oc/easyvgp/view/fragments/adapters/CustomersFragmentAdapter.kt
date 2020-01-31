package com.skichrome.oc.easyvgp.view.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.local.database.Customers

class CustomersFragmentAdapter(private var customers: List<Customers> = emptyList()) :
    RecyclerView.Adapter<CustomersFragmentAdapter.CustomersFragmentViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomersFragmentViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_rv_fragment_customer, parent, false)
        return CustomersFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomersFragmentViewHolder, position: Int) = holder.bind(customers[position])

    override fun getItemCount(): Int = customers.size

    // Update adapter list

    fun replaceList(newCustomersList: List<Customers>)
    {
        customers = newCustomersList
        notifyDataSetChanged()
    }

    class CustomersFragmentViewHolder(private val rvItemView: View) : RecyclerView.ViewHolder(rvItemView)
    {
        fun bind(customers: Customers)
        {
            val textView = rvItemView.findViewById<TextView>(R.id.rvItemCustomerName)
            textView.text = customers.name
        }
    }
}