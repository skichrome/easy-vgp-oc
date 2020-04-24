package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.HomeSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.remote.util.RemoteControlPoint
import com.skichrome.oc.easyvgp.model.remote.util.RemoteMachineType
import com.skichrome.oc.easyvgp.model.remote.util.RemoteMachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.util.*
import kotlinx.coroutines.*

class RemoteHomeSource(private val dispatchers: CoroutineDispatcher = Dispatchers.IO) :
    HomeSource
{
    // =================================
    //              Fields
    // =================================

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllControlPointsAsync(): Deferred<Results<List<ControlPoint>>> = withContext(dispatchers) {
        try
        {
            async {
                val results = getControlPointCollection().get()
                    .await()
                    ?.toObjects(RemoteControlPoint::class.java)?.toList()
                    ?.map { ControlPoint(id = it.id, name = it.name, code = it.code) }

                results?.let { Success(it) }
                    ?: throw RemoteRepositoryException("Something went wrong when fetching all control points")
            }
        }
        catch (e: Exception)
        {
            async { Error(e) }
        }
    }

    override suspend fun getAllMachineTypesAsync(): Deferred<Results<List<MachineType>>> = withContext(dispatchers) {
        async {
            try
            {
                val results = getMachineTypesCollection().get()
                    .await()
                    ?.toObjects(RemoteMachineType::class.java)?.toList()
                    ?.map { MachineType(id = it.id, legalName = it.legalName, name = it.name) }

                results?.let { Success(it) }
                    ?: throw RemoteRepositoryException("Something went wrong when fetching all control points")
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }
    }

    override suspend fun getAllMachineTypeCtrlPointsAsync(): Deferred<Results<List<MachineTypeWithControlPoints>>> = withContext(dispatchers) {
        async {
            try
            {
                val results = getMachineTypesControlPointCollection().get()
                    .await()
                    ?.map {
                        it.toObject<RemoteMachineTypeWithControlPoints>().let { remoteMachineTypeWithControlPoints ->
                            MachineTypeWithControlPoints(
                                machineType = MachineType(
                                    id = remoteMachineTypeWithControlPoints.machineType.id,
                                    name = remoteMachineTypeWithControlPoints.machineType.name,
                                    legalName = remoteMachineTypeWithControlPoints.machineType.legalName
                                ),
                                controlPoints = remoteMachineTypeWithControlPoints.controlPoints.map { remoteCtrlPoint ->
                                    ControlPoint(
                                        id = remoteCtrlPoint.id,
                                        name = remoteCtrlPoint.name,
                                        code = remoteCtrlPoint.code
                                    )
                                }
                            )
                        }
                    }
                results?.let { Success(it) }
                    ?: throw RemoteRepositoryException("Something went wrong when fetching all control points")
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }
    }

    override fun observeHomeReportsEndValidityDate(): LiveData<Results<List<HomeEndValidityReportItem>>> =
        MutableLiveData(Error(NotImplementedException("Not available on remote database for now")))

    override suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>> =
        Error(NotImplementedException("Not available on remote database for now"))

    override suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long> =
        Error(NotImplementedException("Not available on remote database for now"))

    override suspend fun updateUserAndCompany(userAndCompany: UserAndCompany): Results<Int> =
        Error(NotImplementedException("Not available on remote database for now"))

    override suspend fun updateExtraEmailSentStatus(extraId: Long): Results<Int> =
        Error(NotImplementedException("Not available on remote database for now"))

    override suspend fun insertControlPointsAsync(ctrlPoints: List<ControlPoint>): Deferred<Results<List<Long>>> = withContext(dispatchers) {
        async { Error(NotImplementedException("Not available on remote database for home viewModel")) }
    }

    override suspend fun insertMachineTypesAsync(machineTypes: List<MachineType>): Deferred<Results<List<Long>>> = withContext(dispatchers) {
        async { Error(NotImplementedException("Not available on remote database for home viewModel")) }
    }

    override suspend fun insertMachineTypesWithCtrlPoints(machineTypesWithCtrlPoints: List<MachineTypeWithControlPoints>): Results<List<Long>> =
        Error(NotImplementedException("Not available on remote database for home viewModel"))

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