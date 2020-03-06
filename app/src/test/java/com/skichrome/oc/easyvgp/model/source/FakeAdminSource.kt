package com.skichrome.oc.easyvgp.model.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.AdminSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeAdminSource(
    private val machineTypeDataService: LinkedHashMap<Long, MachineType> = LinkedHashMap(),
    private val ctrlPointDataService: LinkedHashMap<Long, ControlPoint> = LinkedHashMap(),
    private val machineTypeControlPointDataService: LinkedHashMap<Long, MachineTypeWithControlPoints> = LinkedHashMap()
) : AdminSource
{
    // =================================
    //              Fields
    // =================================

    private val machineTypeObservable = MutableLiveData<List<MachineType>>()
    private val ctrlPointObservable = MutableLiveData<List<ControlPoint>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeMachineType(): LiveData<Results<List<MachineType>>> = machineTypeObservable.map { Success(it) }
    override fun observeControlPoints(): LiveData<Results<List<ControlPoint>>> = ctrlPointObservable.map { Success(it) }

    override suspend fun getAllMachineType(): Results<List<MachineType>> = Success(machineTypeDataService.values.toList())
    override suspend fun getAllControlPoints(): Results<List<ControlPoint>> = Success(ctrlPointDataService.values.toList())

    override suspend fun insertNewMachineType(machineType: MachineType): Results<Long>
    {
        machineTypeDataService[machineType.id] = machineType
        return Success(machineType.id)
    }

    override suspend fun updateMachineType(machineType: MachineType): Results<Int>
    {
        return if (machineTypeDataService[machineType.id] != null)
        {
            machineTypeDataService[machineType.id] = machineType
            Success(1)
        } else
            Error(ItemNotFoundException("item doesn't exist in this source"))
    }

    override suspend fun insertNewControlPoint(controlPoint: ControlPoint): Results<Long>
    {
        ctrlPointDataService[controlPoint.id] = controlPoint
        return Success(controlPoint.id)
    }

    override suspend fun updateControlPoint(controlPoint: ControlPoint): Results<Int>
    {
        return if (ctrlPointDataService[controlPoint.id] != null)
        {
            ctrlPointDataService[controlPoint.id] = controlPoint
            Success(1)
        } else
            Error(ItemNotFoundException("item doesn't exist in this source"))
    }

    override suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints>
    {
        val result = machineTypeControlPointDataService[id]
        return result?.let { Success(it) } ?: Error(ItemNotFoundException("item doesn't exist in this source"))
    }

    override suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<List<Long>>
    {
        machineTypeControlPointDataService[machineTypeWithControlPoints.machineType.id] = machineTypeWithControlPoints
        return Success(machineTypeWithControlPoints.controlPoints.map { it.id })
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        machineTypeObservable.value = machineTypeDataService.values.toList().sortedBy { it.id }
        ctrlPointObservable.value = ctrlPointDataService.values.toList().sortedBy { it.id }
    }
}