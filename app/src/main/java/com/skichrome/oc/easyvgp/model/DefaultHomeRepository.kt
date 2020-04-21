package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.HomeRepository
import com.skichrome.oc.easyvgp.model.base.HomeSource
import com.skichrome.oc.easyvgp.model.local.database.HomeEndValidityReportItem
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.util.NetworkException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException

class DefaultHomeRepository(private val netManager: NetManager, private val localSource: HomeSource, private val remoteSource: HomeSource) :
    HomeRepository
{
    override fun observeReports(): LiveData<Results<List<HomeEndValidityReportItem>>> = localSource.observeHomeReportsEndValidityDate()

    override suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>> = localSource.getAllUserAndCompany()
    override suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long> = localSource.insertNewUserAndCompany(userAndCompany)
    override suspend fun updateNewUserAndCompany(userAndCompany: UserAndCompany): Results<Int> = localSource.updateUserAndCompany(userAndCompany)

    override suspend fun synchronizeDatabase(): Results<Boolean>
    {
        if (netManager.isConnectedToInternet())
        {
            val remoteCtrlPointsAsync = remoteSource.getAllControlPointsAsync()
            val remoteMachineTypesAsync = remoteSource.getAllMachineTypesAsync()
            val remoteMachineTypeWithCtrlPointsAsync = remoteSource.getAllMachineTypeCtrlPointsAsync()

            val remoteCtrlPoints = remoteCtrlPointsAsync.await()
            val remoteMachineTypes = remoteMachineTypesAsync.await()
            val remoteMachineTypeWithCtrlPoints = remoteMachineTypeWithCtrlPointsAsync.await()

            if (remoteCtrlPoints is Success && remoteMachineTypes is Success && remoteMachineTypeWithCtrlPoints is Success)
            {
                val localCtrlPtResultsAsync = localSource.insertControlPointsAsync(remoteCtrlPoints.data)
                val localMachineTypeResultsAsync = localSource.insertMachineTypesAsync(remoteMachineTypes.data)

                val localCtrlPointResults = localCtrlPtResultsAsync.await()
                val localMachineTypeResults = localMachineTypeResultsAsync.await()

                val localMachineTypeWithCtrlPointsResults = localSource.insertMachineTypesWithCtrlPoints(remoteMachineTypeWithCtrlPoints.data)

                return if (localCtrlPointResults is Success && localMachineTypeResults is Success && localMachineTypeWithCtrlPointsResults is Success)
                    Success(true)
                else
                    Error(Exception("Data was not inserted in local database"))
            }
            else
                return Error(RemoteRepositoryException("Something went wrong when fetching data from remote repository"))

        }
        else
            return Error(NetworkException("Network is unreachable"))
    }
}