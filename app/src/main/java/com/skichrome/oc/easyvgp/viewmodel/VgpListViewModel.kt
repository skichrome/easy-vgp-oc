package com.skichrome.oc.easyvgp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpListRepository
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.uiJob

class VgpListViewModel(private val repository: VgpListRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    override val message: LiveData<Event<Int>>
        get() = _message

    private val _vgpList = MutableLiveData<List<VgpListItem>>()
    val vgpList: LiveData<List<VgpListItem>> = _vgpList

    // =================================
    //        Superclass Methods
    // =================================

    override fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    // =================================
    //              Methods
    // =================================

    fun onClickReport(report: VgpListItem)
    {
    }

    fun loadAllVgpFromMachine(machineId: Long)
    {
        viewModelScope.uiJob {
            val result = repository.getAllReports(machineId)
            if (result is Success)
            {
                _vgpList.value = result.data.groupBy { it.reportDate }.map { it.value.first() }
                Log.e("VgpListVM", "Success: ${result.data}")
            } else
                Log.e("VgpListVM", "An error occurred when loading vgp list", (result as Error).exception)
        }
    }
}