package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.base.AdminRepository
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FakeAndroidTestAdminRepository(
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

    override fun observeMachineType(): LiveData<Results<List<MachineType>>> = observableMachineTypes.map { Results.Success(it) }
    override fun observeControlPoints(): LiveData<Results<List<ControlPoint>>> = observableControlPoints.map { Results.Success(it) }

    override suspend fun getAllMachineType(): Results<List<MachineType>> = Results.Success(machineTypeDataService.values.toList().sortedBy { it.id })
    override suspend fun getAllControlPoints(): Results<List<ControlPoint>> =
        Results.Success(ctrlPointsDataService.values.toList().sortedBy { it.id })

    override suspend fun insertNewMachineType(machineType: MachineType): Results<Long>
    {
        machineTypeDataService[machineType.id] = machineType
        return Results.Success(machineType.id)
    }

    override suspend fun updateMachineType(machineType: MachineType): Results<Int> = when (machineTypeDataService[machineType.id])
    {
        null -> Results.Error(ItemNotFoundException("This item was not fount in the list"))
        else ->
        {
            machineTypeDataService[machineType.id] = machineType
            Results.Success(1)
        }
    }

    override suspend fun insertNewControlPoint(controlPoint: ControlPoint): Results<Long>
    {
        ctrlPointsDataService[controlPoint.id] = controlPoint
        return Results.Success(controlPoint.id)
    }

    override suspend fun updateControlPoint(controlPoint: ControlPoint): Results<Int> = when (machineTypeDataService[controlPoint.id])
    {
        null -> Results.Error(ItemNotFoundException("This item was not fount in the list"))
        else ->
        {
            ctrlPointsDataService[controlPoint.id] = controlPoint
            Results.Success(1)
        }
    }

    override suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints> =
        when (val machWithCtrlPt = machineTypeWithCtrlPtDataService[id])
        {
            null -> Results.Error(ItemNotFoundException("This item was not fount in the list"))
            else ->
            {
                println("Mach found : $machWithCtrlPt")
                Results.Success(machWithCtrlPt)
            }
        }

    override suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<List<Long>>
    {
        machineTypeWithCtrlPtDataService[machineTypeWithControlPoints.machineType.id] = machineTypeWithControlPoints
        return Results.Success(machineTypeWithControlPoints.controlPoints.map { it.id })
    }

    // =================================
    //              Methods
    // =================================

    fun refresh() = runBlocking(Dispatchers.Main) {
        observableMachineTypes.value = machineTypeDataService.values.toList().sortedBy { it.id }
        observableControlPoints.value = ctrlPointsDataService.values.toList().sortedBy { it.id }
    }
}