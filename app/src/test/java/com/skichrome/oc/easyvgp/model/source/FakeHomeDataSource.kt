package com.skichrome.oc.easyvgp.model.source

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.base.HomeSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class FakeHomeDataSource(
    private val userAndCompanyDataService: LinkedHashMap<Long, UserAndCompany> = LinkedHashMap(),
    private val ctrlPointDataService: LinkedHashMap<Long, ControlPoint> = LinkedHashMap(),
    private val machineTypeDataService: LinkedHashMap<Long, MachineType> = LinkedHashMap(),
    private val machineTypeCtrlPointDataService: LinkedHashMap<Long, MachineTypeControlPointCrossRef> = LinkedHashMap()
) : HomeSource
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>> = Success(userAndCompanyDataService.values.toList())

    override suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long>
    {
        userAndCompanyDataService[userAndCompany.company.id] = userAndCompany
        return Success(userAndCompany.company.id)
    }

    override suspend fun updateUserAndCompany(userAndCompany: UserAndCompany): Results<Int>
    {
        val userExist = userAndCompanyDataService[userAndCompany.company.id]
        return if (userExist != null)
        {
            userAndCompanyDataService[userAndCompany.company.id] = userAndCompany
            Success(1)
        } else
            Error(ItemNotFoundException("Item doesnt' exist in the list"))
    }

    override suspend fun getAllControlPointsAsync(): Deferred<Results<List<ControlPoint>>> = withContext(Dispatchers.Unconfined) {
        async { Success(ctrlPointDataService.values.toList().sortedBy { it.id }) }
    }

    override suspend fun getAllMachineTypesAsync(): Deferred<Results<List<MachineType>>> = withContext(Dispatchers.Unconfined) {
        async { Success(machineTypeDataService.values.toList().sortedBy { it.id }) }
    }

    override suspend fun getAllMachineTypeCtrlPointsAsync(): Deferred<Results<List<MachineTypeWithControlPoints>>> = withContext(Dispatchers.Main) {
        async {
            val ctrlPtMachineListPair = mutableListOf<Pair<MachineType, ControlPoint>>()
            machineTypeCtrlPointDataService.values.forEach {
                val ctrlPt = ctrlPointDataService[it.ctrlPointId]
                val machineType = machineTypeDataService[it.machineTypeId]

                if (ctrlPt == null || machineType == null)
                    throw Exception("Warn ! ctrlPt or machineType was not found !")

                ctrlPtMachineListPair.add(Pair(machineType, ctrlPt))
            }
            val results = ctrlPtMachineListPair.groupBy({ it.first }, { it.second })
                .map {
                    MachineTypeWithControlPoints(
                        machineType = it.key,
                        controlPoints = it.value
                    )
                }
            Success(results)
        }
    }

    override suspend fun insertControlPointsAsync(ctrlPoints: List<ControlPoint>): Deferred<Results<List<Long>>> = withContext(Dispatchers.Main) {
        async {
            val result = mutableListOf<Long>()
            ctrlPoints.map {
                ctrlPointDataService[it.id] = it
                result.add(it.id)
            }
            Success(result)
        }
    }

    override suspend fun insertMachineTypesAsync(machineTypes: List<MachineType>): Deferred<Results<List<Long>>> = withContext(Dispatchers.Main) {
        async {
            val result = mutableListOf<Long>()
            machineTypes.map {
                machineTypeDataService[it.id] = it
                result.add(it.id)
            }
            Success(result)
        }
    }

    override suspend fun insertMachineTypesWithCtrlPoints(machineTypesWithCtrlPoints: List<MachineTypeWithControlPoints>): Results<List<Long>>
    {
        val result = mutableListOf<Long>()
        val dataToInsert = mutableListOf<MachineTypeControlPointCrossRef>()
        machineTypesWithCtrlPoints.map { machineTypesWithCtrlPts ->
            machineTypesWithCtrlPts.controlPoints.map { ctrlPoint ->
                dataToInsert.add(
                    MachineTypeControlPointCrossRef(
                        machineTypeId = machineTypesWithCtrlPts.machineType.id,
                        ctrlPointId = ctrlPoint.id
                    )
                )
            }
        }
        dataToInsert.forEachIndexed { index, machineTypeControlPointCrossRef ->
            machineTypeCtrlPointDataService[machineTypeControlPointCrossRef.ctrlPointId] = machineTypeControlPointCrossRef
            result.add(index.toLong())
        }
        return Success(result)
    }

    // =================================
    //              Methods
    // =================================

    fun insertData(
        ctrlPt: LinkedHashMap<Long, ControlPoint>,
        machTypes: LinkedHashMap<Long, MachineType>,
        machTypesCtrlPt: List<MachineTypeControlPointCrossRef>
    )
    {
        ctrlPt.forEach { ctrlPointDataService[it.key] = it.value }
        machTypes.forEach { machineTypeDataService[it.key] = it.value }
        machTypesCtrlPt.forEachIndexed { index, machineTypeControlPointCrossRef ->
            machineTypeCtrlPointDataService[index.toLong()] = machineTypeControlPointCrossRef
        }
    }
}