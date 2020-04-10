package com.skichrome.oc.easyvgp.model

import android.net.Uri
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.LocalRepositoryException
import com.skichrome.oc.easyvgp.util.NetworkException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException
import java.io.File

class DefaultVgpListRepository(
    private val netManager: NetManager,
    private val localSource: VgpListSource,
    private val remoteSource: VgpListSource
) : VgpListRepository
{
    override suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>> = localSource.getAllReports(machineId)

    override suspend fun generateReport(
        userId: Long,
        customerId: Long,
        machineId: Long,
        machineTypeId: Long,
        reportDate: Long
    ): Results<Boolean>
    {
        return if (netManager.isConnectedToInternet())
        {
            val localUser = localSource.getUserFromId(userId)
            val localReport = localSource.getReportFromDate(reportDate)
            val customer = localSource.getCustomerFromId(customerId)
            val machine = localSource.getMachineFromId(machineId)
            val machineType = localSource.getMachineTypeFromId(machineTypeId)

            if (localUser is Success && localReport is Success && customer is Success && machine is Success && machineType is Success)
            {
                machine.data.localPhotoRef?.let { photoRef ->
                    val photoRefUri = Uri.fromFile(File(photoRef))
                    val storedRemoteRef = machine.data.remotePhotoRef

                    val uploadPhotoResult = remoteSource.uploadImageToStorage(
                        userUid = localUser.data.user.firebaseUid,
                        localUri = photoRefUri,
                        oldRemoteUri = storedRemoteRef
                    )
                    if (uploadPhotoResult is Success)
                    {
                        machine.data.remotePhotoRef = uploadPhotoResult.data
                        val updateResult = localSource.updateMachine(machine.data)
                        if (updateResult is Error)
                            return updateResult
                    }
                    else
                        return uploadPhotoResult as? Error ?: Error(RemoteRepositoryException("Something went wrong with remote storage method"))
                }

                localUser.data.user.signaturePath?.let { signatureRef ->
                    if (!localUser.data.user.isSignatureEnabled)
                        return@let

                    val uploadResult = remoteSource.uploadImageToStorage(
                        userUid = localUser.data.user.firebaseUid,
                        localUri = signatureRef,
                        oldRemoteUri = localUser.data.user.remoteSignaturePath
                    )
                    if (uploadResult is Success)
                    {
                        localUser.data.user.remoteSignaturePath = uploadResult.data
                        val updateLocalResult = localSource.updateUser(localUser.data.user)
                        if (updateLocalResult is Error)
                            return updateLocalResult
                    }
                    else
                        return uploadResult as? Error ?: Error(RemoteRepositoryException("Something went wrong with remote storage method"))
                }

                remoteSource.generateReport(
                    user = localUser.data,
                    customer = customer.data,
                    machineType = machineType.data,
                    machine = machine.data,
                    reports = localReport.data,
                    reportDate = reportDate
                )
            }
            else
                Error((localReport as? Error)?.exception ?: LocalRepositoryException("Something went wrong with local source"))
        }
        else
            Error(NetworkException("Internet access is required to upload a report"))
    }
}