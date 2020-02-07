package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentCustomerBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.CustomersFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.Injection
import kotlinx.android.synthetic.main.fragment_customer.*

class CustomerFragment : BaseBindingFragment<FragmentCustomerBinding>()
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
        configureUI()
        configureFab()
        configureRecyclerView()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureUI()
    {
        binding.viewModel = viewModel
        viewModel.customerClick.observe(this, EventObserver {
            if (args.isNewVGPAction)
            {
                // Todo Navigate to New VGP Fragment
            } else
                navigateToAddEditCustomerFragment(it)
        })
    }

    private fun configureFab()
    {
        fragCustomerFab?.setOnClickListener { navigateToAddEditCustomerFragment() }
    }

    private fun configureRecyclerView()
    {
        adapter = CustomersFragmentAdapter(viewModel)
        fragCustomerRecyclerView.adapter = adapter
    }

    // --- Navigation --- //

    private fun navigateToAddEditCustomerFragment(customerId: Long = -1L)
    {
        val opt = CustomerFragmentDirections.actionCustomerFragmentToAddEditCustomerFragment(customerId)
        findNavController().navigate(opt)
    }
}