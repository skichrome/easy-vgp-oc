package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListSource
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.NotImplementedException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException
import com.skichrome.oc.easyvgp.util.await
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class RemoteVgpListSource(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : VgpListSource
{
    // =================================
    //              Fields
    // =================================

    private val storage = Firebase.storage
    private val userReference = storage.reference

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun downloadReportFromStorage(remotePath: String?, destinationFile: File): Results<File> = withContext(dispatchers) {
        return@withContext try
        {
            remotePath?.let { path ->
                val pictureRef = userReference.child(path)
                pictureRef.getFile(destinationFile)
                    .await()
                Success(destinationFile)
            } ?: Error(RemoteRepositoryException("Remote report path is NULL !!"))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override fun observeReports(): LiveData<Results<List<VgpListItem>>> =
        throw NotImplementedException("Not implemented for remote source")

    override suspend fun getMachineCtrlPtExtraFromId(id: Long): Results<MachineControlPointDataExtra> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun updateMachineCtrlPtExtra(extra: MachineControlPointDataExtra): Results<Int> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun loadCustomerEmail(customerId: Long): Results<String> =
        Error(NotImplementedException("Not implemented for remote source"))
}