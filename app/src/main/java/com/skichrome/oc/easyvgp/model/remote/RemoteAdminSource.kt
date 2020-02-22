package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.oc.easyvgp.model.AdminSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.await
import com.skichrome.oc.easyvgp.util.awaitUpload
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteAdminSource(private val db: FirebaseFirestore, private val dispatchers: CoroutineDispatcher = Dispatchers.IO) : AdminSource
{
    // =================================
    //              Fields
    // =================================

    companion object
    {
        const val REMOTE_ADMIN_COLLECTION = "admin"
        const val REMOTE_MACHINE_TYPE_DOCUMENT = "machine_type"
        const val REMOTE_VERSION = "1.0"
    }

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeMachineType(): LiveData<Results<List<MachineType>>> =
        throw NotImplementedError("Not implemented for remote source, use getAllMachineType() instead")

    override suspend fun getAllMachineType(): Results<List<MachineType>> = withContext(dispatchers) {
        return@withContext try
        {
            val result = getMachineTypesCollection().get()
                .await()
                ?.toObjects(RemoteMachineType::class.java)?.toList()
                ?.map {
                    MachineType(id = it.id, remoteId = it.remoteId, legalName = it.legalName, name = it.name)
                }
            return@withContext Success(result)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertOrUpdateMachineType(machineType: List<MachineType>): Results<List<Long>> =
        Error(Exception("Insert machine types list not implemented on remote source, use insertNewMachineType() instead"))

    override suspend fun updateMachineType(machineType: MachineType): Results<Int> =
        throw NotImplementedError("update machine types is automatically done when calling insertNewMachineType")

    override suspend fun insertNewMachineType(machineType: MachineType): Results<String> = withContext(dispatchers) {
        return@withContext try
        {
            val result = getMachineTypesCollection()
                .add(machineType)
                .awaitUpload()
            Success(result.id)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun getMachineTypesCollection() = db.collection(REMOTE_ADMIN_COLLECTION)
        .document(REMOTE_MACHINE_TYPE_DOCUMENT)
        .collection(REMOTE_VERSION)
}
