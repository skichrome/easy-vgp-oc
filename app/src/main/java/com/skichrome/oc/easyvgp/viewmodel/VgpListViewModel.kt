package com.skichrome.oc.easyvgp.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListRepository
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.uiJob

class VgpListViewModel(private val repository: VgpListRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    // --- Event

    private val _isLoading = ObservableBoolean(false)
    val isLoading: ObservableBoolean = _isLoading

    private val _reportDateEvent = MutableLiveData<Event<Long>>()
    val reportDateEvent: LiveData<Event<Long>> = _reportDateEvent

    private val _pdfDataReadyEvent = MutableLiveData<Event<Boolean>>()
    val pdfDataReadyEvent: LiveData<Event<Boolean>> = _pdfDataReadyEvent

    private val _pdfClickEvent = MutableLiveData<Event<VgpListItem>>()
    val pdfClickEvent: LiveData<Event<VgpListItem>> = _pdfClickEvent

    private val _pdfValidClickEvent = MutableLiveData<Event<VgpListItem>>()
    val pdfValidClickEvent: LiveData<Event<VgpListItem>> = _pdfValidClickEvent

    // --- Data

    private val _vgpList = MutableLiveData<List<VgpListItem>>()
    val vgpList: LiveData<List<VgpListItem>> = _vgpList

    // =================================
    //              Methods
    // =================================

    // --- Event

    fun onClickReport(report: VgpListItem)
    {
        if (report.isValid)
            showMessage(R.string.vgp_list_view_model_cannot_edit_validated_report)
        else
            _reportDateEvent.value = Event(report.reportDate)
    }

    fun onClickReportPdf(report: VgpListItem)
    {
        when
        {
            report.reportLocalPath == null && report.isValid ->
            {
                // Todo -> download report from Firebase storage
            }
            report.isValid -> _pdfValidClickEvent.value = Event(report)
            !report.isValid -> _pdfClickEvent.value = Event(report)
        }
    }

    // --- Data

    fun loadAllVgpFromMachine(machineId: Long)
    {
        viewModelScope.uiJob {
            val result = repository.getAllReports(machineId)
            if (result is Success)
                _vgpList.value = result.data.groupBy { it.reportDate }.map { it.value.first() }
            else
                Log.e("VgpListVM", "An error occurred when loading vgp list", (result as? Error)?.exception)
        }
    }

    fun loadReport(userId: Long, customerId: Long, report: VgpListItem, machineId: Long, machineTypeId: Long)
    {
        viewModelScope.uiJob {
            _isLoading.set(true)
            val result = repository.generateReport(
                userId = userId,
                report = report,
                machineTypeId = machineTypeId,
                machineId = machineId,
                customerId = customerId
            )
            if (result is Success)
                _pdfDataReadyEvent.value = Event(result.data)
            else
                Log.e("VgpListVM", "Error when loading report", (result as? Error)?.exception)
            _isLoading.set(false)
        }
    }
}