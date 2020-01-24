package com.skichrome.oc.easyvgp.view.fragments

import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.errorLog
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import com.skichrome.oc.easyvgp.view.base.FragmentNavigation
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.ref.WeakReference

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

    private lateinit var callback: WeakReference<HomeFragmentListener>

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_home
    override fun configureFragment()
    {
        configureCallbackToParentActivity()
        configureBtn()
    }

    private fun configureBtn()
    {
        fragHomeBtnNewVGP.setOnClickListener { callback.get()?.onNavigationRequested(FragmentNavigation.CUSTOMERS) }
    }

    // =================================
    //            Callbacks
    // =================================

    private fun configureCallbackToParentActivity()
    {
        try
        {
            callback = WeakReference(activity as HomeFragmentListener)
        } catch (e: ClassCastException)
        {
            errorLog("You must implement HomeFragmentListener into it's parent activity !!")
        }
    }

    interface HomeFragmentListener
    {
        fun onNavigationRequested(destination: FragmentNavigation)
    }
}