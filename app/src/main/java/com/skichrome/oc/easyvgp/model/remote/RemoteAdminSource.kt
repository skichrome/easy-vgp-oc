package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.oc.easyvgp.model.AdminSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
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
        const val REMOTE_MACHINE_TYPE_CONTROL_POINT_DOCUMENT = "machine_type_control_points"
        const val REMOTE_CONTROL_POINT_DOCUMENT = "control_points"
        const val REMOTE_VERSION = "1.0"
    }

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllMachineType(): Results<List<MachineType>> = withContext(dispatchers) {
        return@withContext try
        {
            val result = getMachineTypesCollection().get()
                .await()
                ?.toObjects(RemoteMachineType::class.java)?.toList()
                ?.map { MachineType(id = it.id, legalName = it.legalName, name = it.name) }
            return@withContext Success(result)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getAllControlPoints(): Results<List<ControlPoint>> = withContext(dispatchers) {
        return@withContext try
        {
            val results = getControlPointCollection().get()
                .await()
                ?.toObjects(RemoteControlPoint::class.java)?.toList()
                ?.map { ControlPoint(id = it.id, name = it.name, code = it.code) }
            return@withContext Success(results)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints> = withContext(dispatchers) {
        return@withContext try
        {
            val results = getMachineTypesControlPointCollection().get()
                .await()
                ?.toObjects(RemoteMachineTypeWithControlPoints::class.java)?.toList()
                ?.filter { it.remoteMachineType.id == id }
                ?.map {
                    MachineTypeWithControlPoints(
                        machineType = MachineType(
                            id = it.remoteMachineType.id,
                            name = it.remoteMachineType.name,
                            legalName = it.remoteMachineType.legalName
                        ),
                        controlPoints = it.controlPoints
                    )
                }
            when (results?.size)
            {
                1 -> Success(results.first())
                0 -> Error(ItemNotFoundException("Item doesn't exist on remote database"))
                else -> Error(Exception("Database doesn't have unique ID for Machine Type"))
            }
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachineType(machineType: MachineType): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            getMachineTypesCollection()
                .document("${machineType.id}")
                .set(machineType)
                .awaitUpload()
            Success(machineType.id)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewControlPoint(controlPoint: ControlPoint): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            getControlPointCollection()
                .document("${controlPoint.id}")
                .set(controlPoint)
                .awaitUpload()
            Success(controlPoint.id)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<Long> =
        withContext(dispatchers) {
            return@withContext try
            {
                getMachineTypesControlPointCollection()
                    .document("${machineTypeWithControlPoints.machineType.id}")
                    .set(machineTypeWithControlPoints)
                    .awaitUpload()
                Success(machineTypeWithControlPoints.machineType.id)
            } catch (e: Exception)
            {
                Error(e)
            }
        }

    override fun observeMachineType(): LiveData<Results<List<MachineType>>> =
        throw NotImplementedError("Not implemented for remote source, use getAllMachineType() instead")

    override fun observeControlPoints(): LiveData<Results<List<ControlPoint>>> =
        throw NotImplementedError("Not implemented for remote source, use getAllControlPoint() instead")

    override suspend fun updateMachineType(machineType: MachineType): Results<Int> =
        throw NotImplementedError("update machine types is automatically done when calling insertNewMachineType")

    override suspend fun updateControlPoint(controlPoint: ControlPoint): Results<Int> =
        throw NotImplementedError("update control point is automatically done when calling insertNewControlPoint")

    // =================================
    //              Methods
    // =================================

    private fun getMachineTypesCollection() = db.collection(REMOTE_ADMIN_COLLECTION)
        .document(REMOTE_MACHINE_TYPE_DOCUMENT)
        .collection(REMOTE_VERSION)

    private fun getControlPointCollection() = db.collection(REMOTE_ADMIN_COLLECTION)
        .document(REMOTE_CONTROL_POINT_DOCUMENT)
        .collection(REMOTE_VERSION)

    private fun getMachineTypesControlPointCollection() = db.collection(REMOTE_ADMIN_COLLECTION)
        .document(REMOTE_MACHINE_TYPE_CONTROL_POINT_DOCUMENT)
        .collection(REMOTE_VERSION)
}
