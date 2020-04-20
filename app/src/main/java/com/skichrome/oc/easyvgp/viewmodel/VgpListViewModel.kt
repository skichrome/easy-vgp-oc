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

    // --- Data

    private val _vgpList = MutableLiveData<List<VgpListItem>>()
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

    fun downloadReport(report: VgpListItem, destinationFile: File)
    {
        viewModelScope.launch {
            _isLoading.set(true)
            Log.e("VgpListVM", "Downloading report...")
            val result = repository.downloadReportFromStorage(report.extrasReference, report.reportRemotePath, destinationFile)
            if (result is Success)
                onClickReportPdf(report)
            else
                handleError(result as? Error)
            _isLoading.set(false)
        }
    }
}