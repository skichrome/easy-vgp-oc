package com.skichrome.oc.easyvgp.model.base

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints

interface AdminSource
{
    fun observeMachineType(): LiveData<Results<List<MachineType>>>
    fun observeControlPoints(): LiveData<Results<List<ControlPoint>>>
    suspend fun getAllMachineType(): Results<List<MachineType>>
    suspend fun getAllControlPoints(): Results<List<ControlPoint>>

    suspend fun insertNewMachineType(machineType: MachineType): Results<Long>
    suspend fun updateMachineType(machineType: MachineType): Results<Int>

    suspend fun insertNewControlPoint(controlPoint: ControlPoint): Results<Long>
    suspend fun updateControlPoint(controlPoint: ControlPoint): Results<Int>

    suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints>
    suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<List<Long>>
}