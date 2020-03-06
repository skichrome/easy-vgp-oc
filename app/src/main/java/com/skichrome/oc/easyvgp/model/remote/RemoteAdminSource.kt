package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.skichrome.oc.easyvgp.BuildConfig
import com.skichrome.oc.easyvgp.model.AdminSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import com.skichrome.oc.easyvgp.util.awaitDocument
import com.skichrome.oc.easyvgp.util.awaitQuery
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
        val REMOTE_ADMIN_COLLECTION = if (BuildConfig.DEBUG) "admin_debug" else "admin"
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
                .awaitQuery()
                ?.toObjects(RemoteMachineType::class.java)?.toList()
                ?.map { MachineType(id = it.id, legalName = it.legalName, name = it.name) }
            return@withContext Success(result)
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
                .awaitQuery()
                ?.toObjects(RemoteControlPoint::class.java)?.toList()
                ?.map { ControlPoint(id = it.id, name = it.name, code = it.code) }
            return@withContext Success(results)
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
                .awaitUpload()
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
                .awaitUpload()
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
                .awaitDocument()
                .toObject<RemoteMachineTypeWithControlPoints>()
                ?.let {
                    MachineTypeWithControlPoints(
                        machineType = MachineType(
                            id = it.remoteMachineType.id,
                            name = it.remoteMachineType.name,
                            legalName = it.remoteMachineType.legalName
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

            if (results != null)
                Success(results)
            else
                Error(ItemNotFoundException("Item doesn't exist on remote database"))
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
                    .awaitUpload()

                getMachineTypesControlPointCollection()
                    .document("${machineTypeWithControlPoints.machineType.id}")
                    .set(machineTypeWithControlPoints)
                    .awaitUpload()

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