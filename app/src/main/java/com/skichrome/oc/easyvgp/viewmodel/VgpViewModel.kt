package com.skichrome.oc.easyvgp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.NewVgpRepository
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.uiJob

class VgpViewModel(private val repository: NewVgpRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    // --- Events

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    private val _onClickCommentEvent = MutableLiveData<Event<Int>>()
    val onClickCommentEvent: LiveData<Event<Int>> = _onClickCommentEvent

    private val _onReportSaved = MutableLiveData<Event<Boolean>>()
    val onReportSaved: LiveData<Event<Boolean>> = _onReportSaved

    // --- Data

    private val _machineTypeWithControlPointsData = MutableLiveData<List<ControlPointDataVgp>>()
    val machineTypeWithControlPointsData: LiveData<List<ControlPointDataVgp>> = _machineTypeWithControlPointsData

    // =================================
    //              Methods
    // =================================

    // --- Events

    private fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    fun onClickCommentEvent(index: Int)
    {
        _onClickCommentEvent.value = Event(index)
    }

    fun onClickRadioBtnEvent(index: Int, state: Int)
    {
        viewModelScope.uiJob {
            _machineTypeWithControlPointsData.value?.get(index)?.choicePossibilityId = state
        }
    }

    // --- Data

    fun setCommentToCtrlPointData(index: Int, comment: String)
    {
        viewModelScope.uiJob {
            _machineTypeWithControlPointsData.value?.get(index)?.comment = comment
        }
    }

    fun getMachineTypeWithControlPoints(machineTypeId: Long)
    {
        viewModelScope.uiJob {
            val result = repository.getAllControlPointsWithMachineType(machineTypeId)
            if (result is Success)
            {
                val newList = mutableListOf<ControlPointDataVgp>()
                result.data.controlPoints.forEach {
                    newList.add(
                        ControlPointDataVgp(
                            controlPoint = it,
                            choicePossibilityId = -1,
                            verificationTypeId = 1
                        )
                    )
                }
                _machineTypeWithControlPointsData.value = newList
            } else
            {
                when ((result as? Error)?.exception)
                {
                    else -> showMessage(R.string.vgp_view_model_machine_type_with_ctrl_points_error)
                }
            }
        }
    }

    fun saveCtrlPointDataList(machineId: Long)
    {
        viewModelScope.uiJob {
            _machineTypeWithControlPointsData.value?.let {
                val result = repository.insertMachineControlPointData(it, machineId)
                if (result is Success)
                    _onReportSaved.value = Event(true)
                else
                    Log.e("VgpVm", "Error : ${(result as Error).exception.message}", result.exception)
            }
        }
    }
}