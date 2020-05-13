package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.viewmodel.source.FakeMachineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MachineViewModelTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeMachineRepository
    private lateinit var viewModel: MachineViewModel

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeMachineRepository(
            machineTypeDataService = DataProvider.machineTypeHashMap,
            machinesDataService = DataProvider.machineHashMap
        )
        repository.refresh()

        viewModel = MachineViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown()
    {
        Dispatchers.resetMain()
    }

    // --- Tests --- //

    @Test
    fun getMachines() = runBlockingTest {
        val machines = DataProvider.machineList.filter { it.customer == DataProvider.customer1Id }

        viewModel.changeCustomerId(DataProvider.customer1Id)
        val result = viewModel.machines.getOrAwaitValue()

        assertThat(result, not(IsEqual(listOf())))
        assertThat(result, IsEqual(machines))
    }

    @Test
    fun getMachineTypes() = runBlockingTest {
        val types = DataProvider.machineTypeList

        val result = viewModel.machineTypes.getOrAwaitValue()

        assertThat(result, not(IsEqual(listOf())))
        assertThat(result, IsEqual(types))
    }

    @Test
    fun onClickSelectMachine() = runBlockingTest {
        val machine = DataProvider.machine1

        viewModel.onClickSelectMachine(machine)
        val eventValue = viewModel.machineClicked.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(machine))
    }

    @Test
    fun onClickEditMachine() = runBlockingTest {
        val machineId = DataProvider.machine1Id

        viewModel.onClickEditMachine(machineId)
        val eventValue = viewModel.machineLongClicked.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(machineId))
    }

    @Test
    fun loadMachineToEdit() = runBlockingTest {
        val machine = DataProvider.machine1

        viewModel.loadMachineToEdit(machineId = machine.machineId)
        val result = viewModel.machine.getOrAwaitValue()

        assertThat(result, IsEqual(machine))
    }

    @Test
    fun saveMachine() = runBlockingTest {
        val machineToSave = DataProvider.machineToInsert

        viewModel.saveMachine(machineToSave)
        val eventValue = viewModel.machineSaved.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(true))
    }

    @Test
    fun updateMachine() = runBlockingTest {
        val machineToUpdate = DataProvider.machine1Edit

        viewModel.updateMachine(machineToUpdate)
        val eventValue = viewModel.machineSaved.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(true))
    }
}