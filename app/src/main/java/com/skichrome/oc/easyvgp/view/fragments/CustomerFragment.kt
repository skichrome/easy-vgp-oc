package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.CustomersFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.Injection
import kotlinx.android.synthetic.main.fragment_customer.*

class CustomerFragment : BaseFragment()
{
    // =================================
    //              Fields
    // =================================

    private val args: CustomerFragmentArgs by navArgs()
    private val viewModel by viewModels<CustomerViewModel> { Injection.provideCustomerViewModelFactory(requireActivity().application) }
    private var adapter by AutoClearedValue<CustomersFragmentAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_customer

    override fun configureFragment()
    {
        configureFab()
        configureViewModel()
        configureRecyclerView()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureFab()
    {
        fragCustomerFab?.setOnClickListener { navigateToAddEditCustomerFragment() }
    }

    private fun configureRecyclerView()
    {
        adapter = CustomersFragmentAdapter(viewModel)
        fragCustomerRecyclerView.adapter = adapter
    }

    private fun configureViewModel()
    {
        viewModel.loadAllCustomers()
    }

    // --- Navigation --- //

    private fun navigateToAddEditCustomerFragment(customerId: Long = -1L)
    {
        val opt = CustomerFragmentDirections.actionCustomerFragmentToAddEditCustomerFragment(customerId)
        findNavController().navigate(opt)
    }
}