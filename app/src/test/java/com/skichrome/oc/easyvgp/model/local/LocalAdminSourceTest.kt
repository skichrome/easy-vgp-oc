package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.local.database.ControlPointDao
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeControlPointCrossRefDao
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeDao
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.util.NotImplementedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class LocalAdminSourceTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var machineTypeDao: MachineTypeDao
    private lateinit var ctrlPointDao: ControlPointDao
    private lateinit var machineTypeCtrlPointDao: MachineTypeControlPointCrossRefDao

    private lateinit var adminSource: LocalAdminSource

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        machineTypeDao = db.machinesTypeDao()
        ctrlPointDao = db.controlPointDao()
        machineTypeCtrlPointDao = db.machineTypeControlPointCrossRefDao()

        adminSource = LocalAdminSource(
            controlPointDao = ctrlPointDao,
            machineTypeControlPointDao = machineTypeCtrlPointDao,
            machineTypeDao = machineTypeDao,
            dispatchers = Dispatchers.Main
        )
    }

    @After
    fun tearDown() = db.close()

    // --- Tests --- //

    @Test
    fun observeMachineType() = runBlocking {
        // Insert machine Types with DAO
        val machineTypeList = DataProvider.machineTypeList
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())

        // Observe machine types with source
        val result = adminSource.observeMachineType().getOrAwaitValue()

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(machineTypeList.size))
        assertThat(result.data, IsEqual(machineTypeList))
    }

    @Test
    fun observeControlPoints() = runBlocking {
        // Insert Control Points with DAO
        val ctrlPointList = DataProvider.ctrlPointList
        ctrlPointDao.insertReplace(*ctrlPointList.toTypedArray())

        // Observe ctrl points from source
        val result = adminSource.observeControlPoints().getOrAwaitValue()

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(ctrlPointList.size))
        assertThat(result.data, IsEqual(ctrlPointList))
    }

    @Test
    fun getControlPointsFromMachineTypeId() = runBlocking {
        // Insert dependencies with DAO
        val machineTypes = DataProvider.machineTypeList
        val ctrlPoints = DataProvider.ctrlPointList
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        ctrlPointDao.insertReplace(*ctrlPoints.toTypedArray())

        // Insert machine type and ctrl point cross ref with DAO
        val machineTypeCtrlPointCrossRef = DataProvider.machineTypeCtrlPointCrossRefList
        machineTypeCtrlPointDao.insertReplace(*machineTypeCtrlPointCrossRef.toTypedArray())

        // get result with source
        val result = adminSource.getControlPointsFromMachineTypeId(machineTypes.first().id)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(DataProvider.machineTypeWithControlPoint1))
    }

    @Test
    fun insertNewMachineType_insertWithSource_GetWithDatabase_shouldBeEqual() = runBlocking {
        // Insert MachineType with source
        val machineType = DataProvider.machineType1
        val insertResult = adminSource.insertNewMachineType(machineType)

        assertThat(insertResult, instanceOf(Success::class.java))

        // Get data with DAO
        val result = machineTypeDao.observeMachineTypes().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(1))
        assertThat(result.first(), IsEqual(machineType))
    }

    @Test
    fun updateMachineType_InsertWithDB_updateWithSource_shouldBeEqualToUpdatedValue() = runBlocking {
        // Insert Machine type with DAO
        val machineType = DataProvider.machineType1
        machineTypeDao.insertReplace(machineType)

        // Update with source
        val machineTypeEdit = DataProvider.machineType1Edit
        val updateResult = adminSource.updateMachineType(machineTypeEdit)

        assertThat(updateResult, instanceOf(Success::class.java))

        // get with database
        val result = machineTypeDao.observeMachineTypes().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(1))
        assertThat(result.first(), not(IsEqual(machineType)))
        assertThat(result.first(), IsEqual(machineTypeEdit))
    }

    @Test
    fun insertNewControlPoint_insertWithSource_GetWithDatabase_shouldBeEqual() = runBlocking {
        // Insert Control point with source
        val ctrlPoint = DataProvider.ctrlPoint1
        val insertResult = adminSource.insertNewControlPoint(ctrlPoint)

        assertThat(insertResult, instanceOf(Success::class.java))

        // Get result with DAO
        val result = ctrlPointDao.observeControlPoints().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(1))
        assertThat(result.first(), IsEqual(ctrlPoint))
    }

    @Test
    fun updateControlPoint_InsertWithDB_updateWithSource_shouldBeEqualToUpdatedValue() = runBlocking {
        // Insert Control point with database
        val ctrlPoint = DataProvider.ctrlPoint1
        ctrlPointDao.insertReplace(ctrlPoint)

        // Update Control point with source
        val ctrlPointEdit = DataProvider.ctrlPoint1Edit
        val updateResult = adminSource.updateControlPoint(ctrlPointEdit)

        assertThat(updateResult, instanceOf(Success::class.java))

        // Get result with DAO
        val result = ctrlPointDao.observeControlPoints().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(1))
        assertThat(result.first(), not(IsEqual(ctrlPoint)))
        assertThat(result.first(), IsEqual(ctrlPointEdit))
    }

    @Test
    fun insertNewMachineTypeControlPoint_insertWithSource_GetWithDB_shouldBeEqual() = runBlocking {
        // Insert dependencies with DAO
        val machineTypes = DataProvider.machineTypeList
        val ctrlPoints = DataProvider.ctrlPointList
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        ctrlPointDao.insertReplace(*ctrlPoints.toTypedArray())

        // Insert cross ref from source, ID returned should be equal to control points inserted
        val machineTypeWithCtrlPoint = DataProvider.machineTypeWithControlPoint1
        val insertResult = adminSource.insertNewMachineTypeControlPoint(machineTypeWithCtrlPoint)

        assertThat(insertResult, instanceOf(Success::class.java))
        assertThat((insertResult as Success).data, `is`(machineTypeWithCtrlPoint.controlPoints.map { it.id }))

        // get result with DAO
        val result = machineTypeDao.getMachineTypeWithControlPointsFromMachineTypeId(machineTypeWithCtrlPoint.machineType.id)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machineTypeWithCtrlPoint))
    }

    @Test
    fun insertNewMachineTypeControlPoint_insertWithSource_insertSameMachineTypeIdWithSourceSecondTime_OldRefShouldBeDeleted() = runBlocking {
        // Insert dependencies with DAO
        val machineTypes = DataProvider.machineTypeList
        val ctrlPoints = DataProvider.ctrlPointList
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        ctrlPointDao.insertReplace(*ctrlPoints.toTypedArray())

        // Insert cross ref from source, ID returned should be equal to control points inserted
        val machineTypeWithCtrlPoint = DataProvider.machineTypeWithControlPoint1
        val insertResult = adminSource.insertNewMachineTypeControlPoint(machineTypeWithCtrlPoint)

        assertThat(insertResult, instanceOf(Success::class.java))
        assertThat((insertResult as Success).data, `is`(machineTypeWithCtrlPoint.controlPoints.map { it.id }))

        // Update cross ref from source
        val machineTypeWithCtrlPointUpdate = DataProvider.machineTypeWithControlPointEdit
        val insertUpdateResult = adminSource.insertNewMachineTypeControlPoint(machineTypeWithCtrlPointUpdate)

        assertThat(insertUpdateResult, instanceOf(Success::class.java))
        assertThat((insertUpdateResult as Success).data.size, `is`(machineTypeWithCtrlPointUpdate.controlPoints.size))

        // get result with DAO
        val result = machineTypeDao.getMachineTypeWithControlPointsFromMachineTypeId(machineTypeWithCtrlPoint.machineType.id)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machineTypeWithCtrlPointUpdate))
    }

    @Test
    fun getAllMachineType_shouldReturnAnError_NotImplementedForLocalAdminSource() = runBlocking {
        val result = adminSource.getAllMachineType()

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NotImplementedException::class.java))
    }

    @Test
    fun getAllControlPoints_shouldReturnAnError_NotImplementedForLocalAdminSource() = runBlocking {
        val result = adminSource.getAllControlPoints()

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NotImplementedException::class.java))
    }
}