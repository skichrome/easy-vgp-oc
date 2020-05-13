package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpRepository
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
import com.skichrome.oc.easyvgp.model.local.database.ControlPointData
import com.skichrome.oc.easyvgp.model.local.database.ControlResult
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.util.Event
import kotlinx.coroutines.launch

class VgpViewModel(private val repository: NewVgpRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    // --- Events

    private val _onClickCommentEvent = MutableLiveData<Event<Pair<Int, String?>>>()
    val onClickCommentEvent: LiveData<Event<Pair<Int, String?>>> = _onClickCommentEvent

    private val _onReportSaved = MutableLiveData<Event<Boolean>>()
    val onReportSaved: LiveData<Event<Boolean>> = _onReportSaved

    // --- Data

    private val _machineTypeWithControlPointsData = MutableLiveData<List<ControlPointDataVgp>>()
    val machineTypeWithControlPointsData: LiveData<List<ControlPointDataVgp>> = _machineTypeWithControlPointsData

    // =================================
    //              Methods
    // =================================

    // --- Events

    fun onClickCommentEvent(index: Int, comment: String?)
    {
        _onClickCommentEvent.value = Event(Pair(index, comment))
    }

    fun onClickRadioBtnEvent(index: Int, state: ChoicePossibility)
    {
        viewModelScope.launch {
            _machineTypeWithControlPointsData.value?.get(index)?.choicePossibility = state
        }
    }

    // --- Data

    fun setCommentToCtrlPointData(index: Int, comment: String)
    {
        viewModelScope.launch {
            _machineTypeWithControlPointsData.value?.get(index)?.comment = comment
        }
    }

    fun getMachineTypeWithControlPoints(machineTypeId: Long)
    {
        viewModelScope.launch {
            val result = repository.getAllControlPointsWithMachineType(machineTypeId)
            if (result is Success)
            {
                val newList = mutableListOf<ControlPointDataVgp>()
                result.data.controlPoints.forEach {
                    newList.add(
                        ControlPointDataVgp(
                            controlPoint = it,
                            choicePossibility = ChoicePossibility.UNKNOWN,
                            verificationType = VerificationType.VISUAL,
                            ctrlPointDataId = 0L
                        )
                    )
                }
                _machineTypeWithControlPointsData.value = newList
            }
            else
                handleError(result as? Error)
        }
    }

    fun loadPreviouslyCreatedReport(reportDate: Long)
    {
        viewModelScope.launch {
            val result = repository.getReportFromDate(reportDate)
            if (result is Success)
            {
                val newList = mutableListOf<ControlPointDataVgp>()
                result.data.forEach {
                    newList.add(
                        ControlPointDataVgp(
                            controlPoint = it.ctrlPoint,
                            choicePossibility = it.ctrlPointData.ctrlPointPossibility,
                            verificationType = it.ctrlPointData.ctrlPointVerificationType,
                            ctrlPointDataId = it.ctrlPointData.id,
                            comment = it.ctrlPointData.comment
                        )
                    )
                }
                _machineTypeWithControlPointsData.value = newList
            }
        }
    }

    private fun saveCtrlPointDataList(machineId: Long, controlExtraId: Long)
    {
        viewModelScope.launch {
            _machineTypeWithControlPointsData.value?.let {
                val canSave = it.firstOrNull { ctrlPtDataVgp -> ctrlPtDataVgp.choicePossibility == ChoicePossibility.UNKNOWN }
                if (canSave != null)
                {
                    showMessage(R.string.vgp_view_model_cannot_save_all_points_not_provided)
                    return@let
                }

                val result = repository.insertMachineControlPointData(it, machineId, controlExtraId)
                if (result is Success)
                    _onReportSaved.value = Event(true)
                else
                    handleError(result as? Error)
            }
        }
    }

    fun updateControlResult(extraId: Long, machineId: Long, controlResult: ControlResult, isUpdateMode: Boolean)
    {
        viewModelScope.launch {
            val result = repository.updateControlResult(extraId, controlResult)
            if (result is Success)
            {
                if (isUpdateMode)
                    updatePreviouslyCreatedReport()
                else
                    saveCtrlPointDataList(machineId, extraId)
            }
        }
    }

    private fun updatePreviouslyCreatedReport()
    {
        viewModelScope.launch {
            _machineTypeWithControlPointsData.value?.let {
                val ctrlPointDataList = it.map { ctrlPointDataVgp ->
                    ControlPointData(
                        id = ctrlPointDataVgp.ctrlPointDataId,
                        ctrlPointPossibility = ctrlPointDataVgp.choicePossibility,
                        comment = ctrlPointDataVgp.comment,
                        ctrlPointRef = ctrlPointDataVgp.controlPoint.id,
                        ctrlPointVerificationType = ctrlPointDataVgp.verificationType
                    )
                }
                val result = repository.updateControlPointData(ctrlPointDataList)
                if (result is Success)
                    _onReportSaved.value = Event(true)
                else
                {
                    showMessage(R.string.vgp_view_model_cannot_update)
                    handleError(result as? Error)
                }
            }
        }
    }
}