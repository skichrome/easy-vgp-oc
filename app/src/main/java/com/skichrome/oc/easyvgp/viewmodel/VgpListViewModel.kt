package com.skichrome.oc.easyvgp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.R
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

    // --- Event

    private val _reportDateEvent = MutableLiveData<Event<Long>>()
    val reportDateEvent: LiveData<Event<Long>> = _reportDateEvent

    // --- Data

    private val _vgpList = MutableLiveData<List<VgpListItem>>()
    val vgpList: LiveData<List<VgpListItem>> = _vgpList

    // =================================
    //              Methods
    // =================================

    fun onClickReport(report: VgpListItem)
    {
        if (report.isValid)
            showMessage(R.string.vgp_list_view_model_cannot_edit_validated_report)
        else
            _reportDateEvent.value = Event(report.reportDate)
    }

    fun loadAllVgpFromMachine(machineId: Long)
    {
        viewModelScope.uiJob {
            val result = repository.getAllReports(machineId)
            if (result is Success)
                _vgpList.value = result.data.groupBy { it.reportDate }.map { it.value.first() }
            else
                Log.e("VgpListVM", "An error occurred when loading vgp list", (result as Error).exception)
        }
    }
}