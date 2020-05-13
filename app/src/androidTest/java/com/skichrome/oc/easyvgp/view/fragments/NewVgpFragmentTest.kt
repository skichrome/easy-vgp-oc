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
import com.skichrome.oc.easyvgp.model.FakeAndroidTestNewVgpRepository
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.recyclerViewHasItem
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class NewVgpFragmentTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var repository: FakeAndroidTestNewVgpRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeAndroidTestNewVgpRepository()
        ServiceLocator.newVgpRepository = repository
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // --- Tests --- //

    @Test
    fun newVgpFragment_uiInfoDisplayed() = runBlockingTest {
        val bundle = NewVgpSetupFragmentArgs(
            machineId = -1L,
            customerId = -1L,
            machineTypeId = -1L,
            reportDateFromDatePicker = -1L,
            reportDateToEdit = -1L
        ).toBundle()
        launchFragmentInContainer<NewVgpFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragVGPItemTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.fragVGPChoiceGoodTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.fragVGPChoiceOkTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.fragVGPChoiceBadTitle)).check(matches(isDisplayed()))
        onView(withText(R.string.frag_vgp_item_title_notes)).check(matches(isDisplayed()))

        // Fab
        onView(withId(R.id.fragVGPFab)).check(matches(isDisplayed()))
        onView(withId(R.id.fragVGPFab)).check(matches(isFocusable()))
        onView(withId(R.id.fragVGPFab)).check(matches(isClickable()))
    }

    @Test
    fun newVgpFragment_controlPointDataLoaded() = runBlockingTest {
        val machTypeWithCtrlPt = AndroidDataProvider.machineTypeWithControlPointList
        val expected = machTypeWithCtrlPt.first().controlPoints.map {
            ControlPointDataVgp(
                controlPoint = it,
                choicePossibility = ChoicePossibility.UNKNOWN,
                verificationType = VerificationType.VISUAL,
                ctrlPointDataId = 0L
            )
        }

        repository.insertForTestsMachineTypeAndCtrlPoints(machTypeWithCtrlPt)

        val bundle = NewVgpSetupFragmentArgs(
            machineId = AndroidDataProvider.machine1Id,
            customerId = AndroidDataProvider.customer1Id,
            machineTypeId = AndroidDataProvider.machineType1Id,
            reportDateFromDatePicker = -1L,
            reportDateToEdit = -1L
        ).toBundle()
        launchFragmentInContainer<NewVgpFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragVGPRecyclerView)).apply {
            // Check that UI elements in recyclerView are present
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointStateViewIndicator)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointName)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityGroup)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityGoodState)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityMediumState)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityBadState)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointComment)))))

            // Check that UI element status are has expected when data is loaded when creating a new report
            for (position in expected.indices)
            {
                check(matches(atRecyclerViewPosition(position, withText(expected[position].controlPoint.name), R.id.rvItemFragVgpCtrlPointName)))
                check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityGoodState)))
                check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityMediumState)))
                check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityBadState)))
            }
        }
    }

    @Test
    fun newVgpFragment_controlPointDataLoaded_reportEditMode() = runBlockingTest {
        val reports = AndroidDataProvider.reportList
        val ctrlPtData = AndroidDataProvider.ctrlPointDataList
        val machTypeWithCtrlPt = AndroidDataProvider.machineTypeWithControlPointList
        val extras = AndroidDataProvider.extraList
        val expected = machTypeWithCtrlPt.first().let {
            it.controlPoints.map { ctrlPt ->
                val ctrlPtDataExpected = AndroidDataProvider.ctrlPointDataList.find { ctrlPtData -> ctrlPtData.ctrlPointRef == ctrlPt.id }!!
                ControlPointDataVgp(
                    ctrlPointDataId = ctrlPtDataExpected.id,
                    comment = ctrlPtDataExpected.comment,
                    controlPoint = ctrlPt,
                    choicePossibility = ctrlPtDataExpected.ctrlPointPossibility,
                    verificationType = ctrlPtDataExpected.ctrlPointVerificationType
                )
            }
        }

        repository.insertForTestsMachineTypeAndCtrlPoints(machTypeWithCtrlPt)
        repository.insertForTestsExtras(extras)
        repository.insertForTestsControlPointData(ctrlPtData)
        repository.insertForTestsReports(reports)
        repository.insertMachineControlPointData(expected, AndroidDataProvider.machine1Id, extras.first().id)

        val bundle = NewVgpSetupFragmentArgs(
            machineId = AndroidDataProvider.machine1Id,
            customerId = AndroidDataProvider.customer1Id,
            machineTypeId = AndroidDataProvider.machineType1Id,
            reportDateFromDatePicker = -1L,
            reportDateToEdit = extras.first().reportDate
        ).toBundle()
        launchFragmentInContainer<NewVgpFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragVGPRecyclerView)).apply {
            // Check that UI elements in recyclerView are present
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointStateViewIndicator)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointName)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityGroup)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityGoodState)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityMediumState)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointPossibilityBadState)))))
            check(matches(recyclerViewHasItem(hasDescendant(withId(R.id.rvItemFragVgpCtrlPointComment)))))

            // Check that UI element status are has expected when data is loaded when creating a new report
            for (position in expected.indices)
            {
                check(matches(atRecyclerViewPosition(position, withText(expected[position].controlPoint.name), R.id.rvItemFragVgpCtrlPointName)))

                // https://medium.com/@miloszlewandowski/espresso-matcher-for-imageview-made-easy-with-android-ktx-977374ca3391
//                expected[position].comment?.let {
//                    check(matches(atRecyclerViewPosition(position, hasDrawable(R.drawable.ic_note_24dp, R.color.secondaryColor), R.id.rvItemFragVgpCtrlPointComment)))
//                } ?: check(matches(atRecyclerViewPosition(position, hasDrawable(R.drawable.ic_note_add_24dp, R.color.secondaryColor), R.id.rvItemFragVgpCtrlPointComment)))

                when (expected[position].choicePossibility)
                {
                    ChoicePossibility.GOOD ->
                    {
                        check(matches(atRecyclerViewPosition(position, isChecked(), R.id.rvItemFragVgpCtrlPointPossibilityGoodState)))
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityMediumState)))
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityBadState)))

                        check(
                            matches(
                                atRecyclerViewPosition(
                                    position, hasColor(R.color.ctrlPointChoiceBE), R.id.rvItemFragVgpCtrlPointStateViewIndicator
                                )
                            )
                        )
                    }
                    ChoicePossibility.MEDIUM ->
                    {
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityGoodState)))
                        check(matches(atRecyclerViewPosition(position, isChecked(), R.id.rvItemFragVgpCtrlPointPossibilityMediumState)))
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityBadState)))

                        check(
                            matches(
                                atRecyclerViewPosition(
                                    position, hasColor(R.color.ctrlPointChoiceEM), R.id.rvItemFragVgpCtrlPointStateViewIndicator
                                )
                            )
                        )
                    }
                    ChoicePossibility.BAD ->
                    {
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityGoodState)))
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityMediumState)))
                        check(matches(atRecyclerViewPosition(position, isChecked(), R.id.rvItemFragVgpCtrlPointPossibilityBadState)))

                        check(
                            matches(
                                atRecyclerViewPosition(
                                    position, hasColor(R.color.ctrlPointChoiceME), R.id.rvItemFragVgpCtrlPointStateViewIndicator
                                )
                            )
                        )
                    }
                    else ->
                    {
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityGoodState)))
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityMediumState)))
                        check(matches(atRecyclerViewPosition(position, not(isChecked()), R.id.rvItemFragVgpCtrlPointPossibilityBadState)))
                    }
                }
            }
        }
    }
}