package com.skichrome.oc.easyvgp.model.local.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValueFromAndroidTests
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MachineTypeDaoTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var machineTypeDao: MachineTypeDao
    private lateinit var controlPointDao: ControlPointDao
    private lateinit var machineTypeControlPointCrossRefDao: MachineTypeControlPointCrossRefDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        machineTypeDao = db.machinesTypeDao()
        controlPointDao = db.controlPointDao()
        machineTypeControlPointCrossRefDao = db.machineTypeControlPointCrossRefDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    // --- Tests --- //

    @Test
    @Throws(Exception::class)
    fun observeMachines_shouldReturnEmptyList() = runBlocking {
        val result = machineTypeDao.observeMachineTypes().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(listOf()))
    }

    @Test
    @Throws(Exception::class)
    fun observeMachines_shouldReturnMachineList() = runBlocking {
        val machineTypeList = AndroidDataProvider.machineTypeList

        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())

        val result = machineTypeDao.observeMachineTypes().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machineTypeList))
    }

    @Test
    @Throws(Exception::class)
    fun getMachineById_insertMachineList_GetOneMachineById() = runBlocking {
        val machineTypeList = AndroidDataProvider.machineTypeList
        val machineType = AndroidDataProvider.machineType2

        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())

        val result = machineTypeDao.getMachineTypeFromId(machineType.id)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machineType))
    }

    @Test
    @Throws(Exception::class)
    fun getMachineTypeWithControlPointsFromMachineTypeId_insertMachineTypeAndCtrlPoints_insertCrossRef_returnOnlyTypeFromCtrlPoint() = runBlocking {
        val machineTypes = AndroidDataProvider.machineTypeList
        val ctrlPoints = AndroidDataProvider.ctrlPointList
        val machineTypeWithCtrlPoints = AndroidDataProvider.machineTypeUpdateWithControlPointList
        val machineTypeWithCtrlPoint = AndroidDataProvider.machineTypeWithControlPoint1

        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        controlPointDao.insertReplace(*ctrlPoints.toTypedArray())
        machineTypeWithCtrlPoints.forEach {
            it.controlPoints.forEach { ctrlPt ->
                machineTypeControlPointCrossRefDao.insertReplace(
                    MachineTypeControlPointCrossRef(
                        machineTypeId = it.machineType.id,
                        ctrlPointId = ctrlPt.id
                    )
                )
            }
        }

        val result = machineTypeDao.getMachineTypeWithControlPointsFromMachineTypeId(machineTypeWithCtrlPoint.machineType.id)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machineTypeWithCtrlPoint))
    }
}