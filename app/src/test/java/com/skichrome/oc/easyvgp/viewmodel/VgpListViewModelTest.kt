package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.viewmodel.source.FakeVgpListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class VgpListViewModelTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeVgpListRepository
    private lateinit var viewModel: VgpListViewModel

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeVgpListRepository(
            reportsDataService = DataProvider.vgpListItemMap,
            customersDataService = DataProvider.customersMap
        )
        repository.refresh()
        viewModel = VgpListViewModel(repository)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown()
    {
        Dispatchers.resetMain()
    }

    // --- Tests --- //

    @Test
    fun onClickReport_reportNotValid() = runBlockingTest {
        val vgpListItem = DataProvider.vgpListItem2

        assertThat(vgpListItem.isValid, `is`(false))

        viewModel.onClickReport(vgpListItem)
        val eventValue = viewModel.reportDateEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(vgpListItem.reportDate))
    }

    @Test
    fun onClickReport_reportValid() = runBlockingTest {
        val vgpListItem = DataProvider.vgpListItem1

        assertThat(vgpListItem.isValid, `is`(true))

        viewModel.onClickReport(vgpListItem)
        val eventValue = viewModel.message.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(R.string.vgp_list_view_model_cannot_edit_validated_report))
    }

    @Test
    fun onClickReportPdf_reportNotValid() = runBlockingTest {
        val vgpListItem = DataProvider.vgpListItem2

        assertThat(vgpListItem.isValid, `is`(false))

        viewModel.onClickReportPdf(vgpListItem)
        val eventValue = viewModel.pdfClickEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(vgpListItem))
    }

    @Test
    fun onClickReportPdf_reportValid() = runBlockingTest {
        val vgpListItem = DataProvider.vgpListItem1

        assertThat(vgpListItem.isValid, `is`(true))

        viewModel.onClickReportPdf(vgpListItem)
        val eventValue = viewModel.pdfValidClickEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(vgpListItem))
    }

    @Test
    fun loadAllVgpFromMachine() = runBlockingTest {
        val machine = DataProvider.machine2
        val vgpListItems = DataProvider.vgpListItemList.filter { it.machineId == machine.machineId }
            .groupBy { it.reportDate }
            .map { it.value.first() }
            .sortedBy { it.reportDate }
        viewModel.loadAllVgpFromMachine(machine.machineId)

        val result = viewModel.vgpList.getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(vgpListItems))
    }

    @Test
    fun downloadReport() = runBlockingTest {
        val vgpListItem = DataProvider.vgpListItem1

        assertThat(vgpListItem.isValid, `is`(true))

        viewModel.downloadReport(vgpListItem, File("/"))
        val eventValue = viewModel.pdfValidClickEvent.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(vgpListItem))
    }

    @Test
    fun loadCustomerEmail() = runBlockingTest {
        val customer = DataProvider.customer3
        val vgpListItem = DataProvider.vgpListItem3

        viewModel.loadCustomerEmail(customerId = customer.id, report = vgpListItem)
        val eventValue = viewModel.customerEmail.getOrAwaitValue().getContentIfNotHandled()

        assertThat(eventValue, IsNot(nullValue()))
        assertThat(eventValue, IsEqual(Pair(customer.email, vgpListItem)))
    }
}