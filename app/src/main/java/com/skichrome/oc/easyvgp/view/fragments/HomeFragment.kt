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
        fragHomeBtnNewVGP.setOnClickListener { navigateToNewVGPFragment(true) }
        fragHomeBtnCustomers.setOnClickListener { navigateToNewVGPFragment() }
        fragHomeBtnSeeVGP.setOnClickListener { }
    }

    // --- Navigation --- //

    private fun navigateToNewVGPFragment(isNewVGPAction: Boolean = false)
    {
        val opt = HomeFragmentDirections.actionHomeFragmentToCustomerFragment(isNewVGPAction)
        findNavController().navigate(opt)
    }
}