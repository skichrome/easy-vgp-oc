package com.skichrome.oc.easyvgp.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.skichrome.oc.easyvgp.util.AutoClearedValue

abstract class BaseBindingFragment<B : ViewDataBinding> : Fragment()
{
    // =================================
    //              Fields
    // =================================

    protected var binding: B by AutoClearedValue()

    // =================================
    //        Superclass Methods
    // =================================

    protected abstract fun getFragmentLayout(): Int
    protected abstract fun configureFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, getFragmentLayout(), container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        configureFragment()
    }
}