package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.ControlResult
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointData
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeNewVgpSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultNewVgpRepositoryTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var localNewVgpSource: FakeNewVgpSource
    private lateinit var repository: DefaultNewVgpRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        localNewVgpSource = FakeNewVgpSource(
            extraDataService = DataProvider.extraMap,
            machineTypeWithCtrlPtDataService = DataProvider.machineTypeWithControlPointMap,
            ctrlPointsData = DataProvider.ctrlPointDataMap
        )

        repository = DefaultNewVgpRepository(localSource = localNewVgpSource)
    }

    // --- Tests --- //

    @Test
    fun getAllControlPointsWithMachineType() = runBlockingTest {
        val machineType = DataProvider.machineType2
        val machineTypeWithControlPoint = DataProvider.machineTypeWithControlPoint2
        val result = repository.getAllControlPointsWithMachineType(machineTypeId = machineType.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(machineTypeWithControlPoint))
    }

    @Test
    fun getReportFromDate() = runBlockingTest {
        val extra = DataProvider.extra1
        val expected = localNewVgpSource.getReportList(extra.reportDate)

        val result = repository.getReportFromDate(extra.reportDate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(expected))
    }

    @Test
    fun insertMachineControlPointData() = runBlockingTest {
        val machine = DataProvider.machine1
        val extra = DataProvider.extra1
        val ctrlPtData = DataProvider.ctrlPointData1.let {
            ControlPointDataVgp(
                comment = it.comment,
                ctrlPointDataId = it.id,
                verificationType = it.ctrlPointVerificationType,
                choicePossibility = it.ctrlPointPossibility,
                controlPoint = DataProvider.ctrlPointList[it.ctrlPointRef.toInt() - 1]
            )
        }
        val expected =
            MachineControlPointData(machineId = machine.machineId, ctrlPointDataId = 0L, machineCtrlPointDataExtra = extra.id)

        val repoResult =
            repository.insertMachineControlPointData(ctrlPointDataVgp = listOf(ctrlPtData), machineId = machine.machineId, controlExtraId = extra.id)
        val srcResult = localNewVgpSource.getMachineCtrlPtDataCrossRef(extra.id)

        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat("Insert ID must be zero to allow room to auto increment ID", (repoResult as Success).data, IsEqual(listOf(0L)))
        assertThat(srcResult, instanceOf(Success::class.java))
        assertThat((srcResult as Success).data, IsEqual(expected))
    }

    @Test
    fun updateControlPointData() = runBlockingTest {
        val ctrlPointData = DataProvider.ctrlPointData1
        val ctrlPointDataEdit = DataProvider.ctrlPointData1Edit

        val result = repository.updateControlPointData(listOf(ctrlPointDataEdit))
        val srcResult = localNewVgpSource.getCtrlPtData(ctrlPointData.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
        assertThat(srcResult, instanceOf(Success::class.java))
        assertThat((srcResult as Success).data, IsEqual(ctrlPointDataEdit))
    }

    @Test
    fun updateControlResult() = runBlockingTest {
        val extra = DataProvider.extra1
        val newCtrlResult = ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED

        assertThat(extra.controlGlobalResult, not(IsEqual(newCtrlResult)))

        val result = repository.updateControlResult(extraId = extra.id, controlResult = newCtrlResult)
        val srcResult = localNewVgpSource.getExtra(extra.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
        assertThat(srcResult, instanceOf(Success::class.java))
        assertThat((srcResult as Success).data.controlGlobalResult, IsEqual(newCtrlResult))
    }
}