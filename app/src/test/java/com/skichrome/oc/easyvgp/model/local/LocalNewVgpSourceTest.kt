package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.source.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class LocalNewVgpSourceTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var companyDao: CompanyDao
    private lateinit var customerDao: CustomerDao
    private lateinit var machineDao: MachineDao
    private lateinit var machineTypeDao: MachineTypeDao
    private lateinit var controlPointDao: ControlPointDao
    private lateinit var ctrlPointDataDao: ControlPointDataDao
    private lateinit var machineTypeCtrlPtsDao: MachineTypeControlPointCrossRefDao
    private lateinit var machineCtrlPtDataDao: MachineControlPointDataDao
    private lateinit var machineCtrlPtDataExtraDao: MachineControlPointDataExtraDao

    private lateinit var localSource: NewVgpSource

    @Before
    fun setUp()
    {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        userDao = db.usersDao()
        companyDao = db.companiesDao()
        customerDao = db.customersDao()
        machineDao = db.machinesDao()
        machineTypeDao = db.machinesTypeDao()
        controlPointDao = db.controlPointDao()
        ctrlPointDataDao = db.controlPointDataDao()
        machineTypeCtrlPtsDao = db.machineTypeControlPointCrossRefDao()
        machineCtrlPtDataDao = db.machineControlPointDataDao()
        machineCtrlPtDataExtraDao = db.machineControlPointDataExtraDao()

        localSource = LocalNewVgpSource(
            machineTypeDao = machineTypeDao,
            ctrlPointDataDao = ctrlPointDataDao,
            machineCtrlPointDataDao = machineCtrlPtDataDao,
            machineCtrlPtExtraDao = machineCtrlPtDataExtraDao,
            dispatcher = Dispatchers.Main
        )
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun getAllControlPointsWithMachineTypeTest_insertWithDb_getWithSource_returnOnlySomeCtrlPointsWithOneMachineType() = runBlocking {
        val ctrlPts = DataProvider.ctrlPointList
        val machineTypes = DataProvider.machineTypeList
        val machineTypeCtrlPts = DataProvider.machineTypeCtrlPointCrossRefList
        val expectedMachineTypeWithCtrlPts = DataProvider.machineTypeWithControlPoint2

        controlPointDao.insertReplace(*ctrlPts.toTypedArray())
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        machineTypeCtrlPtsDao.insertReplace(*machineTypeCtrlPts.toTypedArray())

        val result = localSource.getAllControlPointsWithMachineType(expectedMachineTypeWithCtrlPts.machineType.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(expectedMachineTypeWithCtrlPts))
    }

    @Test
    fun getReportFromDateTest_insertReportsWithDb_GetOneReportFromItsDate() = runBlocking {
        val userAndCompanyList = DataProvider.userCompanyList
        val customers = DataProvider.customerList
        val machineTypes = DataProvider.machineTypeList
        val machines = DataProvider.machineList
        val ctrlPts = DataProvider.ctrlPointList
        val ctrlPtsData = DataProvider.ctrlPointDataList
        val machCtrlPointDataCrossRef = DataProvider.machCtrlPtDataList
        val ctrlPtsDataExtra = DataProvider.extraList
        val expectedReport = DataProvider.report2

        userDao.insertReplace(*userAndCompanyList.map { it.user }.toTypedArray())
        companyDao.insertReplace(*userAndCompanyList.map { it.company }.toTypedArray())
        customerDao.insertReplace(*customers.toTypedArray())
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        machineDao.insertReplace(*machines.toTypedArray())
        controlPointDao.insertReplace(*ctrlPts.toTypedArray())
        ctrlPointDataDao.insertReplace(*ctrlPtsData.toTypedArray())
        machineCtrlPtDataExtraDao.insertReplace(*ctrlPtsDataExtra.toTypedArray())
        machineCtrlPtDataDao.insertReplace(*machCtrlPointDataCrossRef.toTypedArray())

        val test = machineCtrlPtDataExtraDao.getPreviouslyCreatedExtras(expectedReport.ctrlPointDataExtra.reportDate)
        val result = localSource.getReportFromDate(expectedReport.ctrlPointDataExtra.reportDate)

        assertThat(test, IsEqual(ctrlPtsDataExtra.find { it.id == expectedReport.ctrlPointDataExtra.id }))

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(listOf(expectedReport)))
    }

    @Test
    fun insertControlPointDataTest_insertControlPointData_getWithDb_shouldBeSuccessfullyInserted() = runBlocking {
        val ctrlPts = DataProvider.ctrlPointList
        val ctrlPtData = DataProvider.ctrlPointData2

        controlPointDao.insertReplace(*ctrlPts.toTypedArray())
        val result = localSource.insertControlPointData(ctrlPtData)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(ctrlPtData.id))
    }

    @Test
    fun updateControlPointDataTest_insertAndUpdateControlPointData_getWithDb_shouldBeSuccessfullyUpdated() = runBlocking {
        val ctrlPts = DataProvider.ctrlPointList
        val ctrlPtsData = DataProvider.ctrlPointDataList
        val ctrlPtDataEdit = DataProvider.ctrlPointData1Edit

        controlPointDao.insertReplace(*ctrlPts.toTypedArray())
        ctrlPointDataDao.insertReplace(*ctrlPtsData.toTypedArray())
        val result = localSource.updateControlPointData(listOf(ctrlPtDataEdit))

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
    }

    @Test
    fun updateControlResultTest_insertReport_updateGeneralResult_reportResultMustBeUpdated() = runBlocking {
        val extras = DataProvider.extraList
        val extraToUpdate = DataProvider.extra2

        machineCtrlPtDataExtraDao.insertReplace(*extras.toTypedArray())

        val result = localSource.updateControlResult(extraToUpdate.id, ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED)
        val dbResult = machineCtrlPtDataExtraDao.getExtraFromId(extraToUpdate.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))

        assertThat(dbResult.id, `is`(extraToUpdate.id))
        assertThat(dbResult.controlGlobalResult, not(`is`(extraToUpdate.controlGlobalResult)))
        assertThat(dbResult.controlGlobalResult, `is`(ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED))
    }

    @Test
    fun insertMachineCtrlPtDataExtraTest_insertMachineCtrlPtDataExtra_getWithDb_shouldBeSuccessfullyInserted() = runBlocking {
        val extra = DataProvider.extra2

        val insertResult = localSource.insertMachineCtrlPtDataExtra(extra)

        val result = machineCtrlPtDataExtraDao.getExtraFromId(extra.id)

        assertThat(insertResult, instanceOf(Success::class.java))
        assertThat((insertResult as Success).data, `is`(extra.id))
        assertThat(result, IsEqual(extra))
    }

    @Test
    fun insertMachineCtrlPtDataCrossRefTest_insertMachineCtrlPtDataCrossRef_getWithDb_shouldBeSuccessfullyInserted() = runBlocking {

        val userAndCompanyList = DataProvider.userCompanyList
        val customers = DataProvider.customerList
        val machineTypes = DataProvider.machineTypeList
        val machines = DataProvider.machineList
        val ctrlPts = DataProvider.ctrlPointList
        val ctrlPtsData = DataProvider.ctrlPointDataList
        val ctrlPtsDataExtra = DataProvider.extraList
        val machCtrlPointDataCrossRef = DataProvider.machCtrlPtData2

        userDao.insertReplace(*userAndCompanyList.map { it.user }.toTypedArray())
        companyDao.insertReplace(*userAndCompanyList.map { it.company }.toTypedArray())
        customerDao.insertReplace(*customers.toTypedArray())
        machineTypeDao.insertReplace(*machineTypes.toTypedArray())
        machineDao.insertReplace(*machines.toTypedArray())
        controlPointDao.insertReplace(*ctrlPts.toTypedArray())
        ctrlPointDataDao.insertReplace(*ctrlPtsData.toTypedArray())
        machineCtrlPtDataExtraDao.insertReplace(*ctrlPtsDataExtra.toTypedArray())

        val result = localSource.insertMachineCtrlPtDataCrossRef(machCtrlPointDataCrossRef)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1L))
    }
}