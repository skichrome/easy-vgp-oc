package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.CustomersFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.Injection
import kotlinx.android.synthetic.main.fragment_customer.*
import java.lang.ref.WeakReference

class CustomerFragment : BaseFragment()
{
    // =================================
    //              Fields
    // =================================

    private val viewModel by viewModels<CustomerViewModel> { Injection.provideCustomerViewModelFactory(requireActivity().application) }
    private var adapter by AutoClearedValue<CustomersFragmentAdapter>()
    private lateinit var callback: WeakReference<CustomerFragmentListeners>

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_customer

    override fun configureFragment()
    {
        callback = WeakReference(configureCallbackToParentActivity())
        configureFab()
        configureRecyclerView()
        configureViewModel()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureFab()
    {
        fragCustomerFab?.setOnClickListener { callback.get()?.onNewCustomerFabClick() }
    }

    private fun configureRecyclerView()
    {
        adapter = CustomersFragmentAdapter()
        fragCustomerRecyclerView.adapter = adapter
    }

    private fun configureViewModel()
    {
        viewModel.loadAllCustomers()

        viewModel.customers.observe(this, Observer {
            it?.let { list: List<Customers> ->
                toast("Customers : ${list.size}")
                adapter.replaceList(list)
            }
        })
    }

    // =================================
    //            Callbacks
    // =================================

    private fun configureCallbackToParentActivity() =
        activity as? CustomerFragmentListeners ?: throw ClassCastException("Parent activity must implement callback !")

    interface CustomerFragmentListeners
    {
        fun onNewCustomerFabClick()
    }
}