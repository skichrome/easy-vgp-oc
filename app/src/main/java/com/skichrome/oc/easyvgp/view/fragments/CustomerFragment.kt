package com.skichrome.oc.easyvgp.view.fragments

import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseViewModelFragment
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel

class CustomerFragment : BaseViewModelFragment<CustomerViewModel>()
{
    // =================================
    //              Fields
    // =================================

    companion object
    {
        @JvmStatic
        fun newInstance(): CustomerFragment = CustomerFragment()
    }

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_customer
    override fun getViewModelClass(): Class<CustomerViewModel> = CustomerViewModel::class.java

    override fun configureViewModelFragment()
    {
        toast("You are in CustomerFragment !")
    }
}