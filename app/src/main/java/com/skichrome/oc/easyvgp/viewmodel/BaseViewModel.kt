package com.skichrome.oc.easyvgp.viewmodel

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.NetworkException
import com.skichrome.oc.easyvgp.util.NotImplementedException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException

abstract class BaseViewModel : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val logTag = javaClass.simpleName

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    // =================================
    //              Methods
    // =================================

    protected fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    protected fun handleError(e: Error?)
    {
        when (e?.exception)
        {
            is NotImplementedException -> Log.e(logTag, "This method isn't implemented !", e.exception)
            is RemoteRepositoryException -> Log.e(logTag, "Remote repo error", e.exception)
            is NetworkException -> Log.e(logTag, "Repo Error, check your network", e.exception)
            is SQLiteConstraintException -> Log.e(logTag, "Check database data insertion !", e.exception)
            else -> Log.e(logTag, "An error happened when fetching data : ${e?.exception?.javaClass?.simpleName}")
        }
    }
}