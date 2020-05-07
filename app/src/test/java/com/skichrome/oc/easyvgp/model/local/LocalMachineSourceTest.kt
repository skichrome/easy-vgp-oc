package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.MachineSource
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.local.database.CustomerDao
import com.skichrome.oc.easyvgp.model.local.database.MachineDao
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeDao
import com.skichrome.oc.easyvgp.model.source.DataProvider
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
class LocalMachineSourceTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var customerDao: CustomerDao
    private lateinit var machineDao: MachineDao
    private lateinit var machineTypeDao: MachineTypeDao

    private lateinit var customerSource: MachineSource

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

        customerDao = db.customersDao()
        machineDao = db.machinesDao()
        machineTypeDao = db.machinesTypeDao()

        customerSource = LocalMachineSource(
            machineDao = machineDao,
            machineTypeDao = machineTypeDao,
            dispatcher = Dispatchers.Main
        )
    }

    @After
    fun tearDown() = db.close()

    // --- Tests --- //

    @Test
    fun observeMachines() = runBlocking {
        // Insert data with database DAO
        val customers = DataProvider.customerList
        val machineTypes = DataProvider.machineTypeList
        val machines = DataProvider.machineList
        customerDao.insertReplace(*customers.toTypedArray())
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        machineDao.insertReplace(*machines.toTypedArray())

        // Observe results from db with source
        val result = customerSource.observeMachines().getOrAwaitValue()

        // Result must be equal to inserted list
        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(machines.size))
        assertThat(result.data, IsEqual(machines))
    }

    @Test
    fun observeMachineTypes() = runBlocking {
        // Insert data with database DAO
        val machineTypes = DataProvider.machineTypeList
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())

        // Observe results from db with source
        val result = customerSource.observeMachineTypes().getOrAwaitValue()

        // Result must be equal to inserted list
        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(machineTypes.size))
        assertThat(result.data, IsEqual(machineTypes))
    }

    @Test
    fun getMachineById() = runBlocking {
        // Insert data with database DAO
        val customers = DataProvider.customerList
        val machineTypes = DataProvider.machineTypeList
        val machineToInsert = DataProvider.machineToInsert
        customerDao.insertReplace(*customers.toTypedArray())
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        machineDao.insertReplace(machineToInsert)

        // Get results from db with source
        val result = customerSource.getMachineById(machineToInsert.machineId)

        // Result must be equal to inserted list
        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(machineToInsert))
    }

    @Test
    fun insertNewMachine() = runBlocking {
        // Insert data with source
        val customers = DataProvider.customerList
        val machineTypes = DataProvider.machineTypeList
        val machineToInsert = DataProvider.machineToInsert
        customerDao.insertReplace(*customers.toTypedArray())
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        customerSource.insertNewMachine(machineToInsert)

        // Get results from db with database DAO
        val result = machineDao.getMachineById(machineToInsert.machineId)

        // Result must be equal to inserted list
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machineToInsert))
    }

    @Test
    fun updateMachine() = runBlocking {
        // Insert data with database DAO
        val customers = DataProvider.customerList
        val machineTypes = DataProvider.machineTypeList
        customerDao.insertReplace(*customers.toTypedArray())
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        machineDao.insertReplace(DataProvider.machine1)

        // Update data with source
        val machineToUpdate = DataProvider.machine1Edit
        customerSource.updateMachine(machineToUpdate)

        // Get results from db with database DAO
        val result = machineDao.getMachineById(machineToUpdate.machineId)

        // Result must be equal to inserted list
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machineToUpdate))
    }
}