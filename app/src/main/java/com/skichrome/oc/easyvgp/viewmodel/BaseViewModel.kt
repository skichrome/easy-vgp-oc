package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.oc.easyvgp.util.Event

abstract class BaseViewModel : ViewModel()
{
    protected val _message = MutableLiveData<Event<Int>>()
    abstract val message: LiveData<Event<Int>>

    protected abstract fun showMessage(msgRef: Int)
}