package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import com.skichrome.oc.easyvgp.viewmodel.source.FakeNewVgpSetupRepository
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
class NewVgpSetupViewModelTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeNewVgpSetupRepository
    private lateinit var viewModel: NewVgpSetupViewModel

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeNewVgpSetupRepository(
            extraDataService = DataProvider.extraByDateMap
        )
        viewModel = NewVgpSetupViewModel(repository)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown()
    {
        Dispatchers.resetMain()
    }

    // --- Tests --- //

    @Test
    fun loadPreviousNewVgpExtras() = runBlockingTest {
        val extra = DataProvider.extra3

        viewModel.loadPreviousNewVgpExtras(extra.reportDate)
        val result = viewModel.machineWithControlPointsDataExtras.getOrAwaitValue()

        assertThat(result, not(nullValue()))
        assertThat(result, IsEqual(extra))
    }

    @Test
    fun createNewVgpExtras() = runBlockingTest {
        val extra = DataProvider.extra1Edit

        viewModel.createNewVgpExtras(extra)

        val eventValue = viewModel.success.getOrAwaitValue().getContentIfNotHandled()
        val repoResult = repository.getExtra(extra.reportDate)

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(extra.id))
        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((repoResult as Success).data, IsEqual(extra))
    }

    @Test
    fun updateNewVgpExtras() = runBlockingTest {
        val extra = DataProvider.extra1Edit

        viewModel.updateNewVgpExtras(extra)

        val eventValue = viewModel.success.getOrAwaitValue().getContentIfNotHandled()
        val repoResult = repository.getExtra(extra.reportDate)

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(extra.id))
        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((repoResult as Success).data, IsEqual(extra))
    }

    @Test
    fun deleteVgpExtras() = runBlockingTest {
        val extra = DataProvider.extra2

        val repoResult = repository.getExtra(extra.reportDate)

        assertThat(repoResult, instanceOf(Success::class.java))
        assertThat((repoResult as Success).data, IsEqual(extra))

        viewModel.deleteVgpExtras(extra.reportDate)

        val deletedRepoResult = repository.getExtra(extra.reportDate)
        assertThat(deletedRepoResult, instanceOf(Error::class.java))
        assertThat((deletedRepoResult as Error).exception, instanceOf(ItemNotFoundException::class.java))
    }
}