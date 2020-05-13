package com.skichrome.oc.easyvgp.model

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeAdminSource
import com.skichrome.oc.easyvgp.model.source.FakeNetManager
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import com.skichrome.oc.easyvgp.util.NetworkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DefaultAdminRepositoryTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var netManager: FakeNetManager

    private lateinit var localSource: FakeAdminSource
    private lateinit var remoteSource: FakeAdminSource
    private lateinit var adminRepository: DefaultAdminRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        netManager = FakeNetManager(isFakeConnected = false)

        localSource = FakeAdminSource(
            machineTypeDataService = DataProvider.machineTypeHashMap,
            ctrlPointDataService = DataProvider.ctrlPointHashMap,
            machineTypeControlPointDataService = DataProvider.machineTypeWithControlPointMap
        )
        remoteSource = FakeAdminSource()

        localSource.refresh()
        remoteSource.refresh()

        adminRepository = DefaultAdminRepository(
            netManager = netManager,
            localSource = localSource,
            remoteSource = remoteSource
        )
    }

    // --- Tests --- //

    @Test
    fun observeMachineType_shouldReturnAListOfMachineType() = runBlockingTest {
        // get expected result
        val machineTypes = DataProvider.machineTypeList

        // Get machine types from repository
        val resultOffline = adminRepository.observeMachineType().getOrAwaitValue()

        assertThat(resultOffline, instanceOf(Success::class.java))
        assertThat((resultOffline as Success).data, IsEqual(machineTypes))

        // Set mock internet state to true and call same method
        netManager.setIsFakeConnected(true)
        val resultOnline = adminRepository.observeMachineType().getOrAwaitValue()

        assertThat(resultOnline, instanceOf(Success::class.java))
        assertThat((resultOnline as Success).data, IsEqual(machineTypes))

        // Offline and online mode must be the same for observe method
        assertEquals(resultOnline, resultOffline)
    }

    @Test
    fun observeControlPoints_shouldReturnAListOfControlPoints() = runBlockingTest {
        // Get expected result
        val ctrlPoints = DataProvider.ctrlPointList

        // Get control points from repository
        val resultOffline = adminRepository.observeControlPoints().getOrAwaitValue()

        assertThat(resultOffline, instanceOf(Success::class.java))
        assertThat((resultOffline as Success).data, IsEqual(ctrlPoints))

        // Set mock internet state to true and call same method
        netManager.setIsFakeConnected(true)
        val resultOnline = adminRepository.observeControlPoints().getOrAwaitValue()

        assertThat(resultOnline, instanceOf(Success::class.java))
        assertThat((resultOnline as Success).data, IsEqual(ctrlPoints))

        // Offline and online mode must be the same for observe method
        assertEquals(resultOnline, resultOffline)
    }

    @Test
    fun getAllMachineType_online_shouldReturnRemoteMachineTypes() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // populate remote source
        val machineType = DataProvider.machineType1
        remoteSource.insertNewMachineType(machineType)

        // get data with
        val result = adminRepository.getAllMachineType()

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(1))
        assertThat(result.data.first(), IsEqual(machineType))
    }

    @Test
    fun getAllControlPoints_online_shouldReturnRemoteControlPoints() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // populate remote source
        val ctrlPoint = DataProvider.ctrlPoint1
        remoteSource.insertNewControlPoint(ctrlPoint)

        // get data with
        val result = adminRepository.getAllControlPoints()

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(1))
        assertThat(result.data.first(), IsEqual(ctrlPoint))
    }

    @Test
    fun getAllMachineType_offline_shouldReturnNetworkExceptionError() = runBlockingTest {
        val results = adminRepository.getAllMachineType()

        assertThat(results, instanceOf(Error::class.java))
        assertThat((results as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun getAllControlPoints_offline_shouldReturnNetworkExceptionError() = runBlockingTest {
        val results = adminRepository.getAllControlPoints()

        assertThat(results, instanceOf(Error::class.java))
        assertThat((results as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun insertNewMachineType_offlineMode_shouldReturnNetworkError() = runBlockingTest {
        val machineType = DataProvider.machineTypeInsert
        val result = adminRepository.insertNewMachineType(machineType)

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun insertNewMachineType_onlineMode_shouldInsertMachineTypeInRemoteSourceAndUpdateLocalSource() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // Insert machine type with repository
        val machineType = DataProvider.machineTypeInsert
        val result = adminRepository.insertNewMachineType(machineType)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(machineType.id))

        // Get remote data and check that machine type has been inserted
        val remoteData = remoteSource.getAllMachineType()

        assertThat(remoteData, instanceOf(Success::class.java))
        assertThat((remoteData as Success).data.size, `is`(1))
        assertThat(remoteData.data.first(), IsEqual(machineType))

        // Get local data and check that machine type has been inserted
        val localData = localSource.getAllMachineType()

        assertThat(localData, instanceOf(Success::class.java))
        assertThat((localData as Success).data.size, `is`(DataProvider.machineTypeList.size + 1))

        val isLocalInserted = localData.data.find { it.id == machineType.id }

        assertThat(isLocalInserted, IsNot(nullValue()))
        assertThat(isLocalInserted, IsEqual(machineType))

    }

    @Test
    fun updateMachineType_offlineMode_shouldReturnNetworkError() = runBlockingTest {
        val machineType = DataProvider.machineTypeInsert
        val result = adminRepository.insertNewMachineType(machineType)

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun updateMachineType_onlineMode_shouldUpdateMachineTypeInRemoteSourceAndUpdateLocalSource() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // Insert data
        val machineType = DataProvider.machineType1
        adminRepository.insertNewMachineType(machineType)

        // Update data
        val machineTypeToUpdate = DataProvider.machineType1Edit
        val result = adminRepository.updateMachineType(machineTypeToUpdate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))

        // Get remote source data, should contains only updated machine type
        val onlineData = remoteSource.getAllMachineType()

        assertThat(onlineData, instanceOf(Success::class.java))
        assertThat((onlineData as Success).data.size, `is`(1))
        assertThat(onlineData.data.first(), not(IsEqual(machineType)))
        assertThat(onlineData.data.first(), IsEqual(machineTypeToUpdate))

        // Get local source data, should contains only updated machine type
        val offlineData = localSource.getAllMachineType()

        assertThat(offlineData, instanceOf(Success::class.java))
        assertThat((offlineData as Success).data.size, `is`(DataProvider.machineTypeList.size))

        val isInsertedAndUpdated = offlineData.data.find { it.id == machineTypeToUpdate.id }

        assertThat(isInsertedAndUpdated, not(IsEqual(machineType)))
        assertThat(isInsertedAndUpdated, IsEqual(machineTypeToUpdate))
    }

    @Test
    fun insertNewControlPoint_shouldInsertMachineTypeInRemoteSourceAndUpdateLocalSource() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // Insert machine type with repository
        val ctrlPoint = DataProvider.ctrlPointInsert
        val result = adminRepository.insertNewControlPoint(ctrlPoint)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(ctrlPoint.id))

        // Get remote data and check that machine type has been inserted
        val remoteData = remoteSource.getAllControlPoints()

        assertThat(remoteData, instanceOf(Success::class.java))
        assertThat((remoteData as Success).data.size, `is`(1))
        assertThat(remoteData.data.first(), IsEqual(ctrlPoint))

        // Get local data and check that machine type has been inserted
        val localData = localSource.getAllControlPoints()

        assertThat(localData, instanceOf(Success::class.java))
        assertThat((localData as Success).data.size, `is`(DataProvider.ctrlPointList.size + 1))

        val isLocalInserted = localData.data.find { it.id == ctrlPoint.id }

        assertThat(isLocalInserted, IsNot(nullValue()))
        assertThat(isLocalInserted, IsEqual(ctrlPoint))
    }

    @Test
    fun insertNewControlPoint_offlineMode_shouldReturnNetworkError() = runBlockingTest {
        val ctrlPoint = DataProvider.ctrlPointInsert
        val result = adminRepository.insertNewControlPoint(ctrlPoint)

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun updateControlPoint_shouldUpdateMachineTypeInRemoteSourceAndUpdateLocalSource() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // Insert data
        val ctrlPoint = DataProvider.ctrlPoint1
        adminRepository.insertNewControlPoint(ctrlPoint)

        // Update data
        val ctrlPointToUpdate = DataProvider.ctrlPoint1Edit
        val result = adminRepository.updateControlPoint(ctrlPointToUpdate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))

        // Get remote source data, should contains only updated machine type
        val onlineData = remoteSource.getAllControlPoints()

        assertThat(onlineData, instanceOf(Success::class.java))
        assertThat((onlineData as Success).data.size, `is`(1))
        assertThat(onlineData.data.first(), not(IsEqual(ctrlPoint)))
        assertThat(onlineData.data.first(), IsEqual(ctrlPointToUpdate))

        // Get local source data, should contains only updated machine type
        val offlineData = localSource.getAllControlPoints()

        assertThat(offlineData, instanceOf(Success::class.java))
        assertThat((offlineData as Success).data.size, `is`(DataProvider.ctrlPointList.size))

        val isInsertedAndUpdated = offlineData.data.find { it.id == ctrlPointToUpdate.id }

        assertThat(isInsertedAndUpdated, not(IsEqual(ctrlPoint)))
        assertThat(isInsertedAndUpdated, IsEqual(ctrlPointToUpdate))
    }

    @Test
    fun updateControlPoint_offlineMode_shouldReturnNetworkError() = runBlockingTest {
        val ctrlPoint = DataProvider.ctrlPoint1Edit
        val result = adminRepository.updateControlPoint(ctrlPoint)

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun getControlPointsFromMachineTypeId_ItemDoesntExistInRemote() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // Insert data with repository
        val machineTypeWithCtrlPt = DataProvider.machineTypeWithControlPointInsert
        val insertResults = adminRepository.getControlPointsFromMachineTypeId(machineTypeWithCtrlPt.machineType.id)

        assertThat(insertResults, instanceOf(Error::class.java))
        assertThat((insertResults as Error).exception, instanceOf(ItemNotFoundException::class.java))
    }

    @Test
    fun getControlPointsFromMachineTypeId() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // Insert required dependencies to remote source
        remoteSource.insertNewMachineTypeControlPoint(DataProvider.machineTypeWithControlPointInsert)

        // Get data with repository
        val machineTypeWithCtrlPt = DataProvider.machineTypeWithControlPointInsert
        val result = adminRepository.getControlPointsFromMachineTypeId(machineTypeWithCtrlPt.machineType.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(machineTypeWithCtrlPt))

        // Assert that fetched data is inserted in local
        val localResult = localSource.getControlPointsFromMachineTypeId(machineTypeWithCtrlPt.machineType.id)

        assertThat(localResult, instanceOf(Success::class.java))
        assertThat((localResult as Success).data, IsEqual(machineTypeWithCtrlPt))
    }

    @Test
    fun insertNewMachineTypeControlPoint() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        // Insert data with repository
        val machineTypeWithCtrlPtToInsert = DataProvider.machineTypeWithControlPointInsert
        val insertResults = adminRepository.insertNewMachineTypeControlPoint(machineTypeWithCtrlPtToInsert)

        assertThat(insertResults, instanceOf(Success::class.java))
        assertThat((insertResults as Success).data, `is`(machineTypeWithCtrlPtToInsert.controlPoints.map { it.id }))

        // Get online data and assert that it has been successfully inserted
        val onlineResult = remoteSource.getControlPointsFromMachineTypeId(machineTypeWithCtrlPtToInsert.machineType.id)

        assertThat(onlineResult, instanceOf(Success::class.java))
        assertThat((onlineResult as Success).data, IsEqual(machineTypeWithCtrlPtToInsert))

        // Get online data and assert that it has been successfully inserted
        val offlineResult = localSource.getControlPointsFromMachineTypeId(machineTypeWithCtrlPtToInsert.machineType.id)

        assertThat(offlineResult, instanceOf(Success::class.java))
        assertThat((offlineResult as Success).data, IsEqual(machineTypeWithCtrlPtToInsert))
    }

    @Test
    fun getControlPointsFromMachineTypeId_offline_shouldReturnNetworkError() = runBlockingTest {
        val machineTypeWithCtrlPt = DataProvider.machineTypeWithControlPoint1
        val result = adminRepository.getControlPointsFromMachineTypeId(machineTypeWithCtrlPt.machineType.id)

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun insertNewMachineTypeControlPoint_offline_shouldReturnNetworkError() = runBlockingTest {
        val machineTypeWithCtrlPtEdit = DataProvider.machineTypeWithControlPointEdit
        val result = adminRepository.insertNewMachineTypeControlPoint(machineTypeWithCtrlPtEdit)

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NetworkException::class.java))
    }
}