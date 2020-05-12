package com.skichrome.oc.easyvgp.viewmodel.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.AdminRepository
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeAdminRepository(
    private val machineTypeDataService: LinkedHashMap<Long, MachineType> = LinkedHashMap(),
    private val ctrlPointsDataService: LinkedHashMap<Long, ControlPoint> = LinkedHashMap(),
    private val machineTypeWithCtrlPtDataService: LinkedHashMap<Long, MachineTypeWithControlPoints> = LinkedHashMap()
) : AdminRepository
{
    // =================================
    //              Fields
    // =================================

    private val observableMachineTypes = MutableLiveData<List<MachineType>>()
    private val observableControlPoints = MutableLiveData<List<ControlPoint>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeMachineType(): LiveData<Results<List<MachineType>>> = observableMachineTypes.map { Success(it) }
    override fun observeControlPoints(): LiveData<Results<List<ControlPoint>>> = observableControlPoints.map { Success(it) }

    override suspend fun getAllMachineType(): Results<List<MachineType>> = Success(machineTypeDataService.values.toList().sortedBy { it.id })
    override suspend fun getAllControlPoints(): Results<List<ControlPoint>> = Success(ctrlPointsDataService.values.toList().sortedBy { it.id })

    override suspend fun insertNewMachineType(machineType: MachineType): Results<Long>
    {
        machineTypeDataService[machineType.id] = machineType
        return Success(machineType.id)
    }

    override suspend fun updateMachineType(machineType: MachineType): Results<Int> = when (machineTypeDataService[machineType.id])
    {
        null -> Error(ItemNotFoundException("This item was not fount in the list"))
        else ->
        {
            machineTypeDataService[machineType.id] = machineType
            Success(1)
        }
    }

    override suspend fun insertNewControlPoint(controlPoint: ControlPoint): Results<Long>
    {
        ctrlPointsDataService[controlPoint.id] = controlPoint
        return Success(controlPoint.id)
    }

    override suspend fun updateControlPoint(controlPoint: ControlPoint): Results<Int> = when (machineTypeDataService[controlPoint.id])
    {
        null -> Error(ItemNotFoundException("This item was not fount in the list"))
        else ->
        {
            ctrlPointsDataService[controlPoint.id] = controlPoint
            Success(1)
        }
    }

    override suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints> =
        when (val machWithCtrlPt = machineTypeWithCtrlPtDataService[id])
        {
            null -> Error(ItemNotFoundException("This item was not fount in the list"))
            else ->
            {
                println("Mach found : $machWithCtrlPt")
                Success(machWithCtrlPt)
            }
        }

    override suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<List<Long>>
    {
        machineTypeWithCtrlPtDataService[machineTypeWithControlPoints.machineType.id] = machineTypeWithControlPoints
        return Success(machineTypeWithControlPoints.controlPoints.map { it.id })
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        observableMachineTypes.value = machineTypeDataService.values.toList().sortedBy { it.id }
        observableControlPoints.value = ctrlPointsDataService.values.toList().sortedBy { it.id }
    }

    fun getMachineType(id: Long) = when (val machineType = machineTypeDataService[id])
    {
        null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
        else -> Success(machineType)
    }

    fun getCtrlPt(id: Long) = when (val ctrlPt = ctrlPointsDataService[id])
    {
        null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
        else -> Success(ctrlPt)
    }
}