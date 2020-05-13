package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeNewVgpSetupSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultNewVgpSetupRepositoryTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var localSrc: FakeNewVgpSetupSource
    private lateinit var repository: DefaultNewVgpSetupRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        localSrc = FakeNewVgpSetupSource(extraDataService = DataProvider.extraByDateMap)
        repository = DefaultNewVgpSetupRepository(localSource = localSrc)
    }

    // --- Tests --- //

    @Test
    fun getPreviousCtrlPtDataExtraFromDate() = runBlockingTest {
        val extra = DataProvider.extra2

        val result = repository.getPreviousCtrlPtDataExtraFromDate(extra.reportDate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(extra))
    }

    @Test
    fun insertMachineCtrlPtDataExtra() = runBlockingTest {
        val extra = DataProvider.extra1
        val extraToInsert = DataProvider.extra1Edit

        val srcBeforeResult = localSrc.getExtra(extraToInsert.reportDate)
        val result = repository.insertMachineCtrlPtDataExtra(extraToInsert)
        val srcResult = localSrc.getExtra(extra.reportDate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat(srcBeforeResult, instanceOf(Success::class.java))
        assertThat(srcResult, instanceOf(Success::class.java))

        assertThat("Repo insert id result must be equal to inserted extra id", (result as Success).data, IsEqual(extraToInsert.id))
        assertThat("Before repo insertion, data in local src must be equal to not modified extra", (srcBeforeResult as Success).data, IsEqual(extra))
        assertThat("After repo insertion, data in local src must be equal to modified extra", (srcResult as Success).data, IsEqual(extraToInsert))
        assertThat("Local data before and after repo insertion must be different", srcBeforeResult.data, not(IsEqual(srcResult.data)))
    }

    @Test
    fun updateMachineCtrlPtDataExtra() = runBlockingTest {
        val extraEdit = DataProvider.extra1Edit

        val result = repository.updateMachineCtrlPtDataExtra(extraEdit)
        val repoResult = localSrc.getExtra(extraEdit.reportDate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
        assertThat((repoResult as Success).data, IsEqual(extraEdit))
    }

    @Test
    fun deleteMachineCtrlPtDataExtra() = runBlockingTest {
        val extra = DataProvider.extra2

        val result = repository.deleteMachineCtrlPtDataExtra(extra.reportDate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
    }
}