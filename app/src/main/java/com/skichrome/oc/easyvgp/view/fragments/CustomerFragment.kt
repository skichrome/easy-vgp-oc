package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentCustomerBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.CustomerFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.CustomerViewModelFactory
import kotlinx.android.synthetic.main.fragment_customer.*

class CustomerFragment : BaseBindingFragment<FragmentCustomerBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val viewModel by viewModels<CustomerViewModel> {
        CustomerViewModelFactory((requireActivity().application as EasyVGPApplication).customerRepository)
    }

    private var adapter by AutoClearedValue<CustomerFragmentAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_customer

    override fun configureFragment()
    {
        configureViewModel()
        configureUI()
        configureFab()
        configureRecyclerView()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {
        customerLongClick.observe(this@CustomerFragment, EventObserver { navigateToAddEditCustomerFragment(it) })
        errorMessage.observe(this@CustomerFragment, EventObserver { binding.root.snackBar(getString(it)) })
        customerClick.observe(this@CustomerFragment, EventObserver { navigateToVgpFragment(it) })
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    private fun configureFab()
    {
        fragCustomerFab?.setOnClickListener { navigateToAddEditCustomerFragment() }
    }

    private fun configureRecyclerView()
    {
        adapter = CustomerFragmentAdapter(viewModel)
        fragCustomerRecyclerView.adapter = adapter
    }

    // --- Navigation --- //

    private fun navigateToAddEditCustomerFragment(customerId: Long = -1L)
    {
        val opt = CustomerFragmentDirections.actionCustomerFragmentToAddEditCustomerFragment(customerId)
        findNavController().navigate(opt)
    }

    private fun navigateToVgpFragment(customerId: Long)
    {
        val opt = CustomerFragmentDirections.actionCustomerFragmentToMachineFragment(customerId)
        findNavController().navigate(opt)
    }
}