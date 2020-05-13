package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.util.MachineTypeCtrlPtMultiChoiceItems
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.viewmodel.source.FakeAdminRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AdminViewModelTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeAdminRepository
    private lateinit var viewModel: AdminViewModel

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeAdminRepository(
            machineTypeDataService = DataProvider.machineTypeHashMap,
            ctrlPointsDataService = DataProvider.ctrlPointHashMap,
            machineTypeWithCtrlPtDataService = DataProvider.machineTypeWithControlPointMap
        )
        repository.refresh()

        viewModel = AdminViewModel(repository)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown()
    {
        Dispatchers.resetMain()
    }

    // --- Tests --- //

    @Test
    fun getMachineTypes() = runBlockingTest {
        val machineTypes = DataProvider.machineTypeList

        val result = viewModel.machineTypes.getOrAwaitValue()

        assertThat(result, not(IsEqual(listOf())))
        assertThat(result, IsEqual(machineTypes))
    }

    @Test
    fun getAllControlPoints() = runBlockingTest {
        val ctrlPts = DataProvider.ctrlPointList

        val result = viewModel.allControlPoints.getOrAwaitValue()

        assertThat(result, not(IsEqual(listOf())))
        assertThat(result, IsEqual(ctrlPts))
    }

    @Test
    fun onClickMachineType() = runBlockingTest {
        val machineTypeWithCtrlPt = DataProvider.machineTypeWithControlPoint1
        val machineTypeCtrlPtMultiChoiceItems = DataProvider.ctrlPointList.map { ctrlPt ->

            MachineTypeCtrlPtMultiChoiceItems(
                machineType = machineTypeWithCtrlPt.machineType,
                ctrlPoint = ctrlPt,
                isChecked = machineTypeWithCtrlPt.controlPoints.contains(ctrlPt)
            )
        }

        viewModel.onClickMachineType(machineTypeWithCtrlPt.machineType)
        val result = viewModel.controlPointsFromMachineType.getOrAwaitValue()

        assertThat(result, IsEqual(machineTypeCtrlPtMultiChoiceItems))
    }

    @Test
    fun onClickControlPoint() = runBlockingTest {
        val ctrlPt = DataProvider.ctrlPoint2

        viewModel.onClickControlPoint(ctrlPt)
        val eventValue = viewModel.onClickControlPoint.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(ctrlPt))
    }

    @Test
    fun onLongClickMachineType() = runBlockingTest {
        val machineType = DataProvider.machineType2

        viewModel.onLongClickMachineType(machineType)
        val eventValue = viewModel.onLongClickMachineType.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(machineType))
    }

    @Test
    fun insertMachineType() = runBlockingTest {
        val machineTypeToInsert = DataProvider.machineTypeInsert

        viewModel.insertMachineType(machineTypeToInsert)
        val resultMsg = viewModel.message.getOrAwaitValue().getContentIfNotHandled()

        assertThat(resultMsg, IsNot(nullValue()))
        assertThat(resultMsg, instanceOf(Int::class.java))
        assertThat(resultMsg, IsEqual(R.string.admin_view_model_machine_type_insert_success))

        val repoResult = repository.getMachineType(machineTypeToInsert.id)

        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((repoResult as Success).data, IsEqual(machineTypeToInsert))
    }

    @Test
    fun updateMachineType() = runBlockingTest {
        val machineTypeEdit = DataProvider.machineType1Edit

        viewModel.updateMachineType(machineTypeEdit)
        val resultMsg = viewModel.message.getOrAwaitValue().getContentIfNotHandled()

        assertThat(resultMsg, IsNot(nullValue()))
        assertThat(resultMsg, instanceOf(Int::class.java))
        assertThat(resultMsg, IsEqual(R.string.admin_view_model_machine_type_update_success))

        val repoResult = repository.getMachineType(machineTypeEdit.id)

        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((repoResult as Success).data, IsEqual(machineTypeEdit))
    }

    @Test
    fun insertControlPoint() = runBlockingTest {
        val ctrlPtToInsert = DataProvider.ctrlPointInsert

        viewModel.insertControlPoint(ctrlPtToInsert)
        val resultMsg = viewModel.message.getOrAwaitValue().getContentIfNotHandled()

        assertThat(resultMsg, IsNot(nullValue()))
        assertThat(resultMsg, instanceOf(Int::class.java))
        assertThat(resultMsg, IsEqual(R.string.admin_view_model_ctrl_point_insert_success))

        val repoResult = repository.getCtrlPt(ctrlPtToInsert.id)

        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((repoResult as Success).data, IsEqual(ctrlPtToInsert))
    }

    @Test
    fun updateControlPoint() = runBlockingTest {
        val ctrlPtEdit = DataProvider.ctrlPoint1Edit

        viewModel.updateControlPoint(ctrlPtEdit)
        val resultMsg = viewModel.message.getOrAwaitValue().getContentIfNotHandled()

        assertThat(resultMsg, IsNot(nullValue()))
        assertThat(resultMsg, instanceOf(Int::class.java))
        assertThat(resultMsg, IsEqual(R.string.admin_view_model_ctrl_point_update_success))

        val repoResult = repository.getCtrlPt(ctrlPtEdit.id)

        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((repoResult as Success).data, IsEqual(ctrlPtEdit))
    }

    @Test
    fun insertOrUpdateMachineTypeWithControlPoints() = runBlockingTest {
        val machineTypeWithCtrlPts = DataProvider.machineTypeWithControlPointInsert

        viewModel.insertOrUpdateMachineTypeWithControlPoints(machineTypeWithControlPoints = machineTypeWithCtrlPts)
        val resultMsg = viewModel.message.getOrAwaitValue().getContentIfNotHandled()

        assertThat(resultMsg, IsNot(nullValue()))
        assertThat(resultMsg, instanceOf(Int::class.java))
        assertThat(resultMsg, IsEqual(R.string.admin_view_model_machine_type_with_ctrl_pt_updated))
    }
}