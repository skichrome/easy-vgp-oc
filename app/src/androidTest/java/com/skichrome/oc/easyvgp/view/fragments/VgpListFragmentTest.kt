package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.atRecyclerViewPosition
import com.skichrome.oc.easyvgp.hasColor
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import com.skichrome.oc.easyvgp.model.FakeAndroidTestVgpListRepository
import com.skichrome.oc.easyvgp.recyclerViewHasItem
import com.skichrome.oc.easyvgp.util.getDateFormatted
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class VgpListFragmentTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var repository: FakeAndroidTestVgpListRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeAndroidTestVgpListRepository()
        ServiceLocator.vgpListRepository = repository
        repository.refresh()
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // --- Tests --- //

    @Test
    fun vgpListFragment_noData_uiElementDisplayed() = runBlockingTest {
        val bundle = VgpListFragmentArgs().toBundle()

        launchFragmentInContainer<VgpListFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.vgpListFragmentRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.vgpListFragmentFab)).apply {
            check(matches(isDisplayed()))
            check(matches(isClickable()))
            check(matches(isFocusable()))
        }
    }

    @Test
    fun vgpListFragment_dataLoaded_uiElementDisplayed() = runBlockingTest {
        val customers = AndroidDataProvider.customerList
        val vgpListItems = AndroidDataProvider.vgpListItemList
        val machineId = AndroidDataProvider.machine1Id

        val expected = vgpListItems.filter { it.machineId == machineId }
            .groupBy { it.reportDate }
            .map { it.value.first() }
            .sortedBy { it.reportDate }

        repository.insertCustomersForTest(customers)
        repository.insertVgpListItemsForTest(vgpListItems)
        repository.refresh()

        val bundle = VgpListFragmentArgs(
            machineId = machineId,
            customerId = AndroidDataProvider.customer1Id,
            machineTypeId = AndroidDataProvider.machineType1Id
        ).toBundle()

        launchFragmentInContainer<VgpListFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.vgpListFragmentRecyclerView)).apply {
            check(matches(isDisplayed()))

            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointStateViewIndicator)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemVgpListReportName)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemVgpListGeneratePdf)))))

            for (position in expected.indices)
            {
                val validityColor = if (expected[position].isValid) R.color.reportFinal else R.color.reportDraft
                check(matches(atRecyclerViewPosition(position, hasColor(validityColor), R.id.rvItemFragVgpCtrlPointStateViewIndicator)))
                check(
                    matches(
                        atRecyclerViewPosition(
                            position,
                            withText(getDateFormatted(expected[position].reportDate)),
                            R.id.rvItemVgpListReportName
                        )
                    )
                )
            }
        }

        onView(withId(R.id.vgpListFragmentFab)).apply {
            check(matches(isDisplayed()))
            check(matches(isClickable()))
            check(matches(isFocusable()))
        }
    }
}