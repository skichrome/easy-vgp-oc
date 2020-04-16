package com.skichrome.oc.easyvgp.model

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.base.MachineRepository
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeMachineDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DefaultMachineRepositoryTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localSource: FakeMachineDataSource
    private lateinit var repository: MachineRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        localSource = FakeMachineDataSource(
            machineDataService = DataProvider.machineHashMap,
            machineTypeDataService = DataProvider.machineTypeHashMap
        )
        localSource.refresh()

        repository = DefaultMachineRepository(localSource = localSource)
    }

    // --- Tests --- //

    @Test
    fun observeMachines() = runBlockingTest {
        val result = repository.observeMachines().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(DataProvider.machineList.size))
        assertThat(result.data, `is`(DataProvider.machineList))
    }

    @Test
    fun observeMachineTypes() = runBlockingTest {
        val result = repository.observeMachineTypes().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(DataProvider.machineTypeList.size))
        assertThat(result.data, `is`(DataProvider.machineTypeList))
    }

    @Test
    fun getMachineById() = runBlockingTest {
        val machineToGet = DataProvider.machine1
        val result = repository.getMachineById(machineToGet.machineId)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.machineId, `is`(machineToGet.machineId))
        assertThat(result.data, IsEqual(machineToGet))
    }

    @Test
    fun insertNewMachine() = runBlockingTest {
        val machineToInsert = DataProvider.machineToInsert
        val result = repository.insertNewMachine(machineToInsert)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, instanceOf(Long::class.java))
        assertThat(result.data, `is`(machineToInsert.machineId))
    }

    @Test
    fun updateMachine() = runBlockingTest {
        val machineToUpdate = DataProvider.machine1Edit
        val result = repository.updateMachine(machineToUpdate)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, instanceOf(Int::class.java))
        assertThat(result.data, `is`(1))
    }
}