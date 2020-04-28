package com.skichrome.oc.easyvgp.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListRepository
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.Event
import kotlinx.coroutines.launch
import java.io.File

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

    private val _pdfClickEvent = MutableLiveData<Event<VgpListItem>>()
    val pdfClickEvent: LiveData<Event<VgpListItem>> = _pdfClickEvent

    private val _pdfValidClickEvent = MutableLiveData<Event<VgpListItem>>()
    val pdfValidClickEvent: LiveData<Event<VgpListItem>> = _pdfValidClickEvent

    private val _customerEmail = MutableLiveData<Event<Pair<String, VgpListItem>>>()
    val customerEmail: LiveData<Event<Pair<String, VgpListItem>>> = _customerEmail

    // --- Data

    private val _machineId = MutableLiveData<Long>()

    private val _vgpList: LiveData<List<VgpListItem>> = _machineId.switchMap { machineId ->
        repository.observeReports().switchMap { items ->
            if (items is Success)
                filterAndGroupReport(items.data, machineId)
            else
            {
                handleError(items as? Error)
                MutableLiveData()
            }
        }
    }
    val vgpList: LiveData<List<VgpListItem>> = _vgpList

    // =================================
    //              Methods
    // =================================

    // --- Event

    fun onClickReport(report: VgpListItem)
    {
        when
        {
            report.isValid -> showMessage(R.string.vgp_list_view_model_cannot_edit_validated_report)
            else -> _reportDateEvent.value = Event(report.reportDate)
        }
    }

    fun onClickReportPdf(report: VgpListItem)
    {
        when
        {
            report.isValid -> _pdfValidClickEvent.value = Event(report)
            else -> _pdfClickEvent.value = Event(report)
        }
    }

    // --- Data

    private fun filterAndGroupReport(items: List<VgpListItem>, machineId: Long): LiveData<List<VgpListItem>>
    {
        val result = MutableLiveData<List<VgpListItem>>()
        viewModelScope.launch {
            result.value = items
                .filter { it.machineId == machineId }
                .groupBy { it.reportDate }.map { it.value.first() }
        }
        return result
    }

    fun loadAllVgpFromMachine(machineId: Long)
    {
        _machineId.value = machineId
    }

    fun downloadReport(report: VgpListItem, destinationFile: File)
    {
        viewModelScope.launch {
            _isLoading.set(true)
            val result = repository.downloadReportFromStorage(report.extrasReference, report.reportRemotePath, destinationFile)
            if (result is Success)
                onClickReportPdf(report)
            else
                handleError(result as? Error)
            _isLoading.set(false)
        }
    }

    fun loadCustomerEmail(customerId: Long, report: VgpListItem)
    {
        viewModelScope.launch {
            val email = repository.loadCustomerEmail(customerId)
            if (email is Success)
                _customerEmail.value = Event(Pair(email.data, report))
            else
                handleError(email as? Error)
        }
    }
}