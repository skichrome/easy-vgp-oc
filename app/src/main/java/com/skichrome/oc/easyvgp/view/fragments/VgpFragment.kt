package com.skichrome.oc.easyvgp.view.fragments

import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentVgpBinding
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment

class VgpFragment : BaseBindingFragment<FragmentVgpBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args: VgpFragmentArgs by navArgs()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_vgp

    override fun configureFragment()
    {
        toast("Customer id : ${args.machineId}")
    }

    // =================================
    //              Methods
    // =================================
}