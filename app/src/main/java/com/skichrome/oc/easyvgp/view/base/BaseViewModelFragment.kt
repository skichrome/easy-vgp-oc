package com.skichrome.oc.easyvgp.view.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.viewmodel.Injection

abstract class BaseViewModelFragment<V : ViewModel> : BaseFragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var _viewModel: V
    protected val viewModel: V
        get() = _viewModel

    // =================================
    //        Superclass Methods
    // =================================

    protected abstract fun getViewModelClass(): Class<V>
    protected abstract fun configureViewModelFragment()

    @CallSuper
    override fun configureFragment()
    {
        configureViewModel()
        configureViewModelFragment()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        _viewModel = ViewModelProvider(this, Injection.provideViewModelFactory()).get(getViewModelClass())
    }
}