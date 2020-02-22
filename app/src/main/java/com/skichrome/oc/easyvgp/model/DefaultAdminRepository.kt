package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.NetworkException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException

class DefaultAdminRepository(
    private val netManager: NetManager,
    private val localSource: AdminSource,
    private val remoteSource: AdminSource
) : AdminRepository
{
    override fun observeMachineType(): LiveData<Results<List<MachineType>>> = localSource.observeMachineType()

    override suspend fun getAllMachineType(): Results<List<MachineType>>
    {
        return if (netManager.isConnectedToInternet())
        {
            val machinesType = remoteSource.getAllMachineType()
            if (machinesType is Success)
            {
                localSource.insertOrUpdateMachineType(machinesType.data)
                Success(machinesType.data)
            } else
                Error(RemoteRepositoryException("An error occurred when trying to fetch remote machine types"))
        } else
            Error(NetworkException("Network isn't available"))
    }

    override suspend fun insertNewMachineType(machineType: MachineType): Results<String>
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
            val result = remoteSource.updateMachineType(machineType)
            if (result is Success)
                result
            else
                Error(RemoteRepositoryException("An error occurred when trying to update new remote machine types"))
        } else
            Error(NetworkException("Network isn't available"))
    }
}