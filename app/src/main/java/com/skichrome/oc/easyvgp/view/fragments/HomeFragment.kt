package com.skichrome.oc.easyvgp.view.fragments

import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment()
{
    // =================================
    //              Fields
    // =================================

    companion object
    {
        @JvmStatic
        fun newInstance(): HomeFragment = HomeFragment()
    }

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
        fragHomeBtnNewVGP.setOnClickListener { toast("You are trying to create a new VGP ;)") }
    }
}