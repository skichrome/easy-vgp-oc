package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.oc.easyvgp.util.Event

abstract class BaseViewModel : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    // =================================
    //              Methods
    // =================================

    protected fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }
}