package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.AdminSource
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.model.remote.util.RemoteControlPoint
import com.skichrome.oc.easyvgp.model.remote.util.RemoteMachineType
import com.skichrome.oc.easyvgp.model.remote.util.RemoteMachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteAdminSource(private val dispatchers: CoroutineDispatcher = Dispatchers.IO) :
    AdminSource
{
    // =================================
    //              Fields
    // =================================

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllMachineType(): Results<List<MachineType>> = withContext(dispatchers) {
        return@withContext try
        {
            val results = getMachineTypesCollection().get()
                .await()
                ?.toObjects(RemoteMachineType::class.java)?.toList()
                ?.map { MachineType(id = it.id, legalName = it.legalName, name = it.name) }

            results?.let { return@withContext Success(it) }
                ?: throw RemoteRepositoryException("Something went wrong when fetching all machine types")
        }
        catch (e: Exception)
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

            results?.let { return@withContext Success(results) }
                ?: throw RemoteRepositoryException("Something went wrong when fetching all control points")
        }
        catch (e: Exception)
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
                .await()
            Success(machineType.id)
        }
        catch (e: Exception)
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
                .await()
            Success(controlPoint.id)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints> = withContext(dispatchers) {
        return@withContext try
        {
            val results = getMachineTypesControlPointCollection()
                .document("$id")
                .get()
                .await()
                .toObject<RemoteMachineTypeWithControlPoints>()
                ?.let {
                    MachineTypeWithControlPoints(
                        machineType = MachineType(
                            id = it.machineType.id,
                            name = it.machineType.name,
                            legalName = it.machineType.legalName
                        ),
                        controlPoints = it.controlPoints.map { remoteCtrlPt ->
                            ControlPoint(
                                id = remoteCtrlPt.id,
                                code = remoteCtrlPt.code,
                                name = remoteCtrlPt.name
                            )
                        }
                    )
                }

            results?.let { return@withContext Success(results) }
                ?: throw ItemNotFoundException("Item doesn't exist on remote database")
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<List<Long>> =
        withContext(dispatchers) {
            return@withContext try
            {
                getMachineTypesControlPointCollection()
                    .document("${machineTypeWithControlPoints.machineType.id}")
                    .delete()
                    .await()

                getMachineTypesControlPointCollection()
                    .document("${machineTypeWithControlPoints.machineType.id}")
                    .set(machineTypeWithControlPoints)
                    .await()

                val idList = machineTypeWithControlPoints.controlPoints.map { it.id }
                Success(idList)
            }
            catch (e: Exception)
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