package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.*
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpRepository
import com.skichrome.oc.easyvgp.model.local.database.ControlPointChoicePossibility
import com.skichrome.oc.easyvgp.model.local.database.ControlPointVerificationType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.uiJob

class VgpViewModel(private val repository: VgpRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    // --- Events

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    private val _onClickCommentEvent = MutableLiveData<Event<Long>>()
    val onClickCommentEvent: LiveData<Event<Long>> = _onClickCommentEvent

    // --- Data

    private val _choicePossibilities: LiveData<List<ControlPointChoicePossibility>> = repository.observeChoicePossibilities().map { choices ->
        if (choices is Success)
            return@map choices.data
        else
        {
            showMessage(R.string.vgp_view_model_choices_error)
            return@map emptyList<ControlPointChoicePossibility>()
        }
    }
    val choicePossibilities: LiveData<List<ControlPointChoicePossibility>> = _choicePossibilities

    private val _verificationTypes: LiveData<List<ControlPointVerificationType>> = repository.observeVerificationsType().map { choices ->
        if (choices is Success)
            return@map choices.data
        else
        {
            showMessage(R.string.vgp_view_model_verifications_error)
            return@map emptyList<ControlPointVerificationType>()
        }
    }
    val verificationTypes: LiveData<List<ControlPointVerificationType>> = _verificationTypes

    private val _machineTypeWithControlPoints = MutableLiveData<MachineTypeWithControlPoints>()
    val machineTypeWithControlPoints: LiveData<MachineTypeWithControlPoints> = _machineTypeWithControlPoints

    // =================================
    //              Methods
    // =================================

    // --- Events

    private fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    fun onClickCommentEvent(id: Long)
    {
        _onClickCommentEvent.value = Event(id)
    }

    // --- Data

    fun getMachineTypeWithControlPoints(machineTypeId: Long)
    {
        viewModelScope.uiJob {
            val result = repository.getAllControlPointsWithMachineType(machineTypeId)
            if (result is Success)
                _machineTypeWithControlPoints.value = result.data
            else
            {
                when ((result as? Error)?.exception)
                {
                    else -> showMessage(R.string.vgp_view_model_machine_type_with_ctrl_points_error)
                }
            }
        }
    }
}