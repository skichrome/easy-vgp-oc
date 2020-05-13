package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListRepository
import com.skichrome.oc.easyvgp.model.base.VgpListSource
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
    override fun observeReports(): LiveData<Results<List<VgpListItem>>> = localSource.observeReports()

    override suspend fun downloadReportFromStorage(extraId: Long, remotePath: String?, destinationFile: File): Results<File> =
        if (netManager.isConnectedToInternet())
        {
            val result = remoteSource.downloadReportFromStorage(remotePath, destinationFile)
            if (result is Success)
            {
                val extras = localSource.getMachineCtrlPtExtraFromId(extraId)
                if (extras is Success)
                {
                    extras.data.reportLocalPath = result.data.path.split("/").last()
                    val localResult = localSource.updateMachineCtrlPtExtra(extras.data)
                    if (localResult is Error)
                        localResult
                    else
                        result
                }
                else
                    extras as? Error ?: Error(LocalRepositoryException("Something went wrong with local VGP List source"))
            }
            else
                result as? Error ?: Error(RemoteRepositoryException("Something went wrong with remote VGP List source"))
        }
        else
            Error(NetworkException("VGP List need network access to work properly"))

    override suspend fun loadCustomerEmail(customerId: Long): Results<String> = localSource.loadCustomerEmail(customerId)
}