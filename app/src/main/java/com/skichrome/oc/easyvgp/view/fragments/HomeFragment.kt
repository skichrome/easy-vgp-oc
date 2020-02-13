package com.skichrome.oc.easyvgp.view.fragments

import androidx.navigation.fragment.findNavController
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment()
{
    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_home
    override fun configureFragment()
    {
        configureBtn()
    }

    private fun configureBtn()
    {
        fragHomeBtnNewVGP.setOnClickListener { navigateToCustomersFragment(true) }
        fragHomeBtnCustomers.setOnClickListener { navigateToCustomersFragment() }
        fragHomeBtnSeeVGP.setOnClickListener { navigateToNewVGPFragment() }
    }

    // --- Navigation --- //

    private fun navigateToCustomersFragment(isNewVGPAction: Boolean = false)
    {
        val opt = HomeFragmentDirections.actionHomeFragmentToCustomerFragment(isNewVGPAction)
        findNavController().navigate(opt)
    }

    private fun navigateToNewVGPFragment() = findNavController().navigate(R.id.action_homeFragment_to_machineFragment)
}