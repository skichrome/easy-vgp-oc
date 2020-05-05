package com.skichrome.oc.easyvgp.model.local.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValueFromAndroidTests
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MachineDaoTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var machineDao: MachineDao
    private lateinit var customerDao: CustomerDao
    private lateinit var machineTypeDao: MachineTypeDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        machineDao = db.machinesDao()
        customerDao = db.customersDao()
        machineTypeDao = db.machinesTypeDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    // --- Tests --- //

    @Test
    @Throws(Exception::class)
    fun observeMachines_shouldReturnEmptyList() = runBlocking {
        val result = machineDao.observeMachines().getOrAwaitValueFromAndroidTests()

        MatcherAssert.assertThat(result, IsNot(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result, IsEqual(listOf()))
    }

    @Test
    @Throws(Exception::class)
    fun observeMachines_shouldReturnMachineList() = runBlocking {
        val machineList = AndroidDataProvider.machineList
        val machineTypeList = AndroidDataProvider.machineTypeList
        val customerList = AndroidDataProvider.customerList

        customerDao.insertReplace(*customerList.toTypedArray())
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())
        machineDao.insertReplace(*machineList.toTypedArray())

        val result = machineDao.observeMachines().getOrAwaitValueFromAndroidTests()

        MatcherAssert.assertThat(result, IsNot(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result, IsEqual(machineList))
    }

    @Test
    @Throws(Exception::class)
    fun getMachineById_insertMachineList_GetOneMachineById() = runBlocking {
        val machineList = AndroidDataProvider.machineList
        val machine = AndroidDataProvider.machine2
        val machineTypeList = AndroidDataProvider.machineTypeList
        val customerList = AndroidDataProvider.customerList

        customerDao.insertReplace(*customerList.toTypedArray())
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())
        machineDao.insertReplace(*machineList.toTypedArray())

        val result = machineDao.getMachineById(machine.machineId)

        MatcherAssert.assertThat(result, IsNot(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result, IsEqual(machine))
    }
}