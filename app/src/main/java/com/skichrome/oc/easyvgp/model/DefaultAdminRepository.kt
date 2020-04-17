package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.AdminRepository
import com.skichrome.oc.easyvgp.model.base.AdminSource
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import com.skichrome.oc.easyvgp.util.NetworkException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException

class DefaultAdminRepository(
    private val netManager: NetManager,
    private val localSource: AdminSource,
    private val remoteSource: AdminSource
) : AdminRepository
{
    override fun observeMachineType(): LiveData<Results<List<MachineType>>> = localSource.observeMachineType()
    override fun observeControlPoints(): LiveData<Results<List<ControlPoint>>> = localSource.observeControlPoints()

    override suspend fun getAllMachineType(): Results<List<MachineType>>
    {
        return if (netManager.isConnectedToInternet())
        {
            val machinesType = remoteSource.getAllMachineType()
            if (machinesType is Success)
            {
                machinesType.data.forEach { localSource.insertNewMachineType(it) }
                Success(machinesType.data)
            } else
                Error(RemoteRepositoryException("An error occurred when trying to fetch remote machine types"))
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun getAllControlPoints(): Results<List<ControlPoint>>
    {
        return if (netManager.isConnectedToInternet())
        {
            val controlPoints = remoteSource.getAllControlPoints()
            if (controlPoints is Success)
            {
                controlPoints.data.forEach { controlPoint -> localSource.insertNewControlPoint(controlPoint) }
                Success(controlPoints.data)
            } else
                Error(RemoteRepositoryException("An error occurred when trying to fetch remote machine types"))
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun insertNewMachineType(machineType: MachineType): Results<Long>
    {
        return if (netManager.isConnectedToInternet())
        {
            val result = remoteSource.insertNewMachineType(machineType)
            if (result is Success)
            {
                getAllMachineType()
                result
            } else
                Error(RemoteRepositoryException("An error occurred when trying to insert new remote machine types"))
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun updateMachineType(machineType: MachineType): Results<Int>
    {
        return if (netManager.isConnectedToInternet())
        {
            val result = remoteSource.insertNewMachineType(machineType)
            if (result is Success)
                localSource.updateMachineType(machineType)
            else
                Error(RemoteRepositoryException("An error occurred when trying to update new remote machine types"))
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun insertNewControlPoint(controlPoint: ControlPoint): Results<Long>
    {
        return if (netManager.isConnectedToInternet())
        {
            val results = remoteSource.insertNewControlPoint(controlPoint)
            if (results is Success)
            {
                getAllControlPoints()
                results
            } else
                Error(RemoteRepositoryException("An error occurred when trying to insert new remote control point"))
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun updateControlPoint(controlPoint: ControlPoint): Results<Int>
    {
        return if (netManager.isConnectedToInternet())
        {
            val results = remoteSource.insertNewControlPoint(controlPoint)
            if (results is Success)
                localSource.updateControlPoint(controlPoint)
            else
                Error(RemoteRepositoryException("An error occurred when trying to update new remote machine types"))
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints>
    {
        return if (netManager.isConnectedToInternet())
        {
            val results = remoteSource.getControlPointsFromMachineTypeId(id)
            if (results is Success)
            {
                localSource.insertNewMachineTypeControlPoint(results.data)
                Success(results.data)
            } else
                when ((results as? Error)?.exception)
                {
                    is ItemNotFoundException -> results
                    else -> Error(RemoteRepositoryException("An error occurred when trying to fetch remote control points"))
                }
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<List<Long>>
    {
        return if (netManager.isConnectedToInternet())
        {
            val results = remoteSource.insertNewMachineTypeControlPoint(machineTypeWithControlPoints)
            if (results is Success)
            {
                localSource.insertNewMachineTypeControlPoint(machineTypeWithControlPoints)
                results
            } else
                Error(RemoteRepositoryException("An error occurred when trying to insert machineTypeWithControlPoint"))

        } else
            Error(NetworkException("Network isn't available"))
    }
}