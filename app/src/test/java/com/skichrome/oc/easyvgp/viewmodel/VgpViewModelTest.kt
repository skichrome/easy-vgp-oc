package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
import com.skichrome.oc.easyvgp.model.local.database.ControlResult
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.viewmodel.source.FakeVgpRepository
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
class VgpViewModelTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeVgpRepository
    private lateinit var viewModel: VgpViewModel

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeVgpRepository(
            machineCtrlPtDataDataService = DataProvider.machCtrlPtDataMap,
            reportsDataService = DataProvider.reportMap,
            extraDataService = DataProvider.extraMap,
            ctrlPtDataService = DataProvider.ctrlPointDataMap,
            machineTypeWithCtrlPtsDataService = DataProvider.machineTypeWithControlPointMap
        )
        viewModel = VgpViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown()
    {
        Dispatchers.resetMain()
    }

    // --- Tests --- //

    @Test
    fun onClickCommentEvent() = runBlockingTest {
        val expected = Pair<Int, String?>(0, null)

        viewModel.onClickCommentEvent(expected.first, expected.second)
        val eventValue = viewModel.onClickCommentEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(expected))
    }

    @Test
    fun onClickRadioBtnEvent() = runBlockingTest {
        val machine = DataProvider.machine1
        val choice = ChoicePossibility.BAD

        viewModel.getMachineTypeWithControlPoints(machine.machineId)
        viewModel.onClickRadioBtnEvent(0, choice)
        val result = viewModel.machineTypeWithControlPointsData.getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result.first().choicePossibility, IsEqual(choice))
    }

    @Test
    fun setCommentToCtrlPointData() = runBlockingTest {
        val machine = DataProvider.machine1
        val comment = "- New comment from tests -"

        viewModel.getMachineTypeWithControlPoints(machine.machineId)
        viewModel.setCommentToCtrlPointData(0, comment)
        val result = viewModel.machineTypeWithControlPointsData.getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result.first().comment, IsEqual(comment))
    }

    @Test
    fun getMachineTypeWithControlPoints() = runBlockingTest {
        val machTypeWithCtrlPt = DataProvider.machineTypeWithControlPoint1
        val expected = machTypeWithCtrlPt.controlPoints.map {
            ControlPointDataVgp(
                controlPoint = it,
                choicePossibility = ChoicePossibility.UNKNOWN,
                verificationType = VerificationType.VISUAL,
                ctrlPointDataId = 0L
            )
        }

        viewModel.getMachineTypeWithControlPoints(machTypeWithCtrlPt.machineType.id)

        val result = viewModel.machineTypeWithControlPointsData.getOrAwaitValue()
        assertThat(result, IsNot(nullValue()))
        assertThat(result, not(IsEqual(listOf())))
        assertThat(result, IsEqual(expected))
    }

    @Test
    fun loadPreviouslyCreatedReport() = runBlockingTest {
        val machTypeWithCtrlPt = DataProvider.machineTypeWithControlPoint1
        val extra = DataProvider.extra1

        val expected = machTypeWithCtrlPt.let {
            it.controlPoints.map { ctrlPt ->
                val ctrlPtData = DataProvider.ctrlPointDataList.find { ctrlPtData -> ctrlPtData.ctrlPointRef == ctrlPt.id }!!
                ControlPointDataVgp(
                    ctrlPointDataId = ctrlPtData.id,
                    comment = ctrlPtData.comment,
                    controlPoint = ctrlPt,
                    choicePossibility = ctrlPtData.ctrlPointPossibility,
                    verificationType = ctrlPtData.ctrlPointVerificationType
                )
            }
        }

        viewModel.loadPreviouslyCreatedReport(extra.reportDate)

        val result = viewModel.machineTypeWithControlPointsData.getOrAwaitValue()
        assertThat(result, IsNot(nullValue()))
        assertThat(result, not(IsEqual(listOf())))
        assertThat(result, IsEqual(expected))
    }

    @Test
    fun updateControlResult_updateModeOn() = runBlockingTest {
        val extra = DataProvider.extra1
        val machine = DataProvider.machine1

        viewModel.getMachineTypeWithControlPoints(machine.machineId)
        viewModel.updateControlResult(
            extraId = extra.id,
            machineId = machine.machineId,
            controlResult = ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED,
            isUpdateMode = true
        )

        val eventValue = viewModel.onReportSaved.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(true))
    }

    @Test
    fun updateControlResult_updateModeOff() = runBlockingTest {
        val extra = DataProvider.extra1
        val machine = DataProvider.machine1

        viewModel.loadPreviouslyCreatedReport(extra.reportDate)
        viewModel.updateControlResult(
            extraId = extra.id,
            machineId = machine.machineId,
            controlResult = ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED,
            isUpdateMode = false
        )

        val eventValue = viewModel.onReportSaved.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(true))
    }
}