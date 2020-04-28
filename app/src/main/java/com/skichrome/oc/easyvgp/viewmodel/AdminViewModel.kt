package com.skichrome.oc.easyvgp.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.AdminRepository
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.model.local.util.MachineTypeCtrlPtMultiChoiceItems
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.NetworkException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException
import com.skichrome.oc.easyvgp.util.uiJob
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    // --- Events

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    private val _onLongClickMachineType = MutableLiveData<Event<MachineType>>()
    val onLongClickMachineType: LiveData<Event<MachineType>> = _onLongClickMachineType

    private val _onClickControlPoint = MutableLiveData<Event<ControlPoint>>()
    val onClickControlPoint: LiveData<Event<ControlPoint>> = _onClickControlPoint

    private val _forceRefresh = MutableLiveData<Boolean>()

    val isLoading = ObservableBoolean(false)

    // --- Data

    private val _machineTypes: LiveData<List<MachineType>> = _forceRefresh.switchMap { refresh ->
        isLoading.set(true)
        if (refresh)
            viewModelScope.uiJob {
                val result = repository.getAllMachineType()
                if (result !is Success)
                    showMessage(R.string.admin_view_model_machine_type_list_refresh_error)
                _forceRefresh.value = false
            }

        repository.observeMachineType().map {
            if (it is Success)
            {
                return@map it.data
            } else
            {
                showMessage(R.string.admin_view_model_machine_type_list_error)
                return@map emptyList<MachineType>()
            }
        }.also { isLoading.set(false) }
    }
    val machineTypes: LiveData<List<MachineType>> = _machineTypes

    private val _allControlPoints: LiveData<List<ControlPoint>> = _forceRefresh.switchMap { refresh ->
        if (refresh)
            viewModelScope.launch {
                val result = repository.getAllControlPoints()
                if (result is Error)
                    showMessage(R.string.admin_view_model_ctrl_point_list_refresh_error)
            }
        repository.observeControlPoints().map {
            if (it is Success)
                return@map it.data
            else
                return@map emptyList<ControlPoint>()
        }
    }

    val allControlPoints: LiveData<List<ControlPoint>> = _allControlPoints

    private val _controlPointsFromMachineType = MutableLiveData<List<MachineTypeCtrlPtMultiChoiceItems>>()
    val controlPointsFromMachineType: LiveData<List<MachineTypeCtrlPtMultiChoiceItems>> = _controlPointsFromMachineType

    // =================================
    //              Methods
    // =================================

    init
    {
        forceRefresh()
    }

    // --- Events

    private fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    fun onClickMachineType(machineType: MachineType)
    {
        loadControlPointsByMachineType(machineType)
    }

    fun onClickControlPoint(ctrlPoint: ControlPoint)
    {
        _onClickControlPoint.value = Event(ctrlPoint)
    }

    fun onLongClickMachineType(machineType: MachineType)
    {
        _onLongClickMachineType.value = Event(machineType)
    }

    private fun forceRefresh()
    {
        _forceRefresh.value = true
    }

    // --- Data

    fun insertMachineType(machineType: MachineType)
    {
        viewModelScope.uiJob {
            val result = repository.insertNewMachineType(machineType)

            if (result is Success)
                showMessage(R.string.admin_view_model_machine_type_insert_success)
            else
                when ((result as? Error)?.exception)
                {
                    is NetworkException -> showMessage(R.string.admin_view_model_network_error)
                    is RemoteRepositoryException -> showMessage(R.string.admin_view_model_remote_repo_error)
                    else -> showMessage(R.string.admin_view_model_machine_type_insert_error)
                }
        }
    }

    fun updateMachineType(machineType: MachineType)
    {
        viewModelScope.uiJob {
            val result = repository.updateMachineType(machineType)
            if (result is Success)
                showMessage(R.string.admin_view_model_machine_type_update_success)
            else
                when ((result as? Error)?.exception)
                {
                    is NetworkException -> showMessage(R.string.admin_view_model_network_error)
                    is RemoteRepositoryException -> showMessage(R.string.admin_view_model_remote_repo_error)
                    else -> showMessage(R.string.admin_view_model_machine_type_update_error)
                }
        }
    }

    fun insertControlPoint(controlPoint: ControlPoint)
    {
        viewModelScope.uiJob {
            val result = repository.insertNewControlPoint(controlPoint)
            if (result is Success)
                showMessage(R.string.admin_view_model_ctrl_point_insert_success)
            else
                when ((result as? Error)?.exception)
                {
                    is NetworkException -> showMessage(R.string.admin_view_model_network_error)
                    is RemoteRepositoryException -> showMessage(R.string.admin_view_model_remote_repo_error)
                    else -> showMessage(R.string.admin_view_model_ctrl_point_insert_error)
                }
        }
    }

    fun updateControlPoint(controlPoint: ControlPoint)
    {
        viewModelScope.uiJob {
            val result = repository.updateControlPoint(controlPoint)
            if (result is Success)
                showMessage(R.string.admin_view_model_ctrl_point_update_success)
            else
                when ((result as? Error)?.exception)
                {
                    is NetworkException -> showMessage(R.string.admin_view_model_network_error)
                    is RemoteRepositoryException -> showMessage(R.string.admin_view_model_remote_repo_error)
                    else -> showMessage(R.string.admin_view_model_ctrl_point_update_error)
                }
        }
    }

    private fun loadControlPointsByMachineType(machineType: MachineType)
    {
        viewModelScope.uiJob {
            isLoading.set(true)

            val resultList = LinkedHashMap<Long, MachineTypeCtrlPtMultiChoiceItems>()

            val allCtrlPoints = repository.getAllControlPoints()
            if (allCtrlPoints is Success)
            {
                allCtrlPoints.data.forEach { ctrlPoints ->
                    resultList[ctrlPoints.id] = MachineTypeCtrlPtMultiChoiceItems(
                        machineType = machineType,
                        ctrlPoint = ctrlPoints,
                        isChecked = false
                    )
                }
                when (val ctrlPointsByMachineId = repository.getControlPointsFromMachineTypeId(machineType.id))
                {
                    is Success ->
                    {
                        ctrlPointsByMachineId.data.controlPoints.forEach { ctrlPoints ->
                            resultList[ctrlPoints.id]?.isChecked = true
                        }
                    }
                    is Error ->
                        Log.e("AdmVM", "data : ${ctrlPointsByMachineId.exception.localizedMessage}", ctrlPointsByMachineId.exception)
                }
                _controlPointsFromMachineType.value = resultList.values.toList()
            }
            isLoading.set(false)
        }
    }

    fun insertOrUpdateMachineTypeWithControlPoints(machineTypeWithControlPoints: MachineTypeWithControlPoints)
    {
        viewModelScope.uiJob {
            val result = repository.insertNewMachineTypeControlPoint(machineTypeWithControlPoints)
            if (result !is Success)
                when ((result as? Error)?.exception)
                {
                    is NetworkException -> showMessage(R.string.admin_view_model_network_error)
                    is RemoteRepositoryException -> showMessage(R.string.admin_view_model_remote_repo_error)
                    else -> showMessage(R.string.admin_view_model_machine_ctrl_point_insert_error)
                }
        }
    }
}