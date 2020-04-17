package com.skichrome.oc.easyvgp.model.base

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import kotlinx.coroutines.Deferred

interface HomeSource
{
    suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>>
    suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long>
    suspend fun updateUserAndCompany(userAndCompany: UserAndCompany): Results<Int>

    suspend fun getAllControlPointsAsync(): Deferred<Results<List<ControlPoint>>>
    suspend fun getAllMachineTypesAsync(): Deferred<Results<List<MachineType>>>
    suspend fun getAllMachineTypeCtrlPointsAsync(): Deferred<Results<List<MachineTypeWithControlPoints>>>

    suspend fun insertControlPointsAsync(ctrlPoints: List<ControlPoint>): Deferred<Results<List<Long>>>
    suspend fun insertMachineTypesAsync(machineTypes: List<MachineType>): Deferred<Results<List<Long>>>
    suspend fun insertMachineTypesWithCtrlPoints(machineTypesWithCtrlPoints: List<MachineTypeWithControlPoints>): Results<List<Long>>
}