package com.skichrome.oc.easyvgp.model.local.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValueFromAndroidTests
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MachineControlPointDataDaoTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var companyDao: CompanyDao
    private lateinit var userDao: UserDao
    private lateinit var customerDao: CustomerDao
    private lateinit var machineDao: MachineDao
    private lateinit var machineTypeDao: MachineTypeDao
    private lateinit var controlPointDao: ControlPointDao
    private lateinit var controlPointDataDao: ControlPointDataDao
    private lateinit var machineControlPointDataDao: MachineControlPointDataDao
    private lateinit var machineControlPointDataExtraDao: MachineControlPointDataExtraDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        companyDao = db.companiesDao()
        userDao = db.usersDao()
        customerDao = db.customersDao()
        machineDao = db.machinesDao()
        machineTypeDao = db.machinesTypeDao()
        controlPointDao = db.controlPointDao()
        controlPointDataDao = db.controlPointDataDao()
        machineControlPointDataDao = db.machineControlPointDataDao()
        machineControlPointDataExtraDao = db.machineControlPointDataExtraDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    // --- Tests --- //

    @Test
    @Throws(Exception::class)
    fun observeControlPointsData_shouldReturnEmptyList() = runBlocking {
        val result = machineControlPointDataDao.observeCtrlPtData().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(listOf()))
    }

    @Test
    @Throws(Exception::class)
    fun observeEndValidityReport_shouldReturnEmptyList() = runBlocking {
        val result = machineControlPointDataDao.observeEndValidityReports().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(listOf()))
    }

    @Test
    @Throws(Exception::class)
    fun observeControlPointsData_insertCtrlPtDataList_shouldReturnVgpListItemList() = runBlocking {
        val userAndCompany = AndroidDataProvider.userCompanyList
        val ctrlPtDataList = AndroidDataProvider.machCtrlPtDataList
        val ctrlPtList = AndroidDataProvider.ctrlPtList
        val customerList = AndroidDataProvider.customerList
        val machineList = AndroidDataProvider.machineList
        val machineTypeList = AndroidDataProvider.machineTypeList
        val ctrlPtData = AndroidDataProvider.ctrlPointDataList
        val extraList = AndroidDataProvider.extraList

        companyDao.insertReplace(*userAndCompany.map { it.company }.toTypedArray())
        userDao.insertReplace(*userAndCompany.map { it.user }.toTypedArray())
        customerDao.insertReplace(*customerList.toTypedArray())
        controlPointDao.insertReplace(*ctrlPtList.toTypedArray())
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())
        machineDao.insertReplace(*machineList.toTypedArray())
        controlPointDataDao.insertReplace(*ctrlPtData.toTypedArray())
        machineControlPointDataExtraDao.insertReplace(*extraList.toTypedArray())

        machineControlPointDataDao.insertIgnore(*ctrlPtDataList.toTypedArray())

        val result = machineControlPointDataDao.observeCtrlPtData().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(AndroidDataProvider.vgpListItemList))
    }

    @Test
    @Throws(Exception::class)
    fun observeEndValidityReport_insertCtrlPtDataList_shouldReturnHomeEndValidityReportItemList() = runBlocking {
        val userAndCompany = AndroidDataProvider.userCompanyList
        val ctrlPtDataList = AndroidDataProvider.machCtrlPtDataList
        val ctrlPtList = AndroidDataProvider.ctrlPtList
        val customerList = AndroidDataProvider.customerList
        val machineList = AndroidDataProvider.machineList
        val machineTypeList = AndroidDataProvider.machineTypeList
        val ctrlPtData = AndroidDataProvider.ctrlPointDataList
        val extraList = AndroidDataProvider.extraList

        companyDao.insertReplace(*userAndCompany.map { it.company }.toTypedArray())
        userDao.insertReplace(*userAndCompany.map { it.user }.toTypedArray())
        customerDao.insertReplace(*customerList.toTypedArray())
        controlPointDao.insertReplace(*ctrlPtList.toTypedArray())
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())
        machineDao.insertReplace(*machineList.toTypedArray())
        controlPointDataDao.insertReplace(*ctrlPtData.toTypedArray())
        machineControlPointDataExtraDao.insertReplace(*extraList.toTypedArray())

        machineControlPointDataDao.insertIgnore(*ctrlPtDataList.toTypedArray())

        val result = machineControlPointDataDao.observeEndValidityReports().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(AndroidDataProvider.homeEndValidityReportItemList))
    }

    @Test
    @Throws(Exception::class)
    fun getPreviouslyInsertedReport_insertReports_shouldReturnReportByDate() = runBlocking {
        val report = AndroidDataProvider.report2

        val userAndCompany = AndroidDataProvider.userCompanyList
        val ctrlPtDataList = AndroidDataProvider.machCtrlPtDataList
        val ctrlPtList = AndroidDataProvider.ctrlPtList
        val customerList = AndroidDataProvider.customerList
        val machineList = AndroidDataProvider.machineList
        val machineTypeList = AndroidDataProvider.machineTypeList
        val ctrlPtData = AndroidDataProvider.ctrlPointDataList
        val extraList = AndroidDataProvider.extraList

        companyDao.insertReplace(*userAndCompany.map { it.company }.toTypedArray())
        userDao.insertReplace(*userAndCompany.map { it.user }.toTypedArray())
        customerDao.insertReplace(*customerList.toTypedArray())
        controlPointDao.insertReplace(*ctrlPtList.toTypedArray())
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())
        machineDao.insertReplace(*machineList.toTypedArray())
        controlPointDataDao.insertReplace(*ctrlPtData.toTypedArray())
        machineControlPointDataExtraDao.insertReplace(*extraList.toTypedArray())

        machineControlPointDataDao.insertIgnore(*ctrlPtDataList.toTypedArray())

        val result = machineControlPointDataDao.getPreviouslyInsertedReport(report.ctrlPointDataExtra.reportDate)
        assertThat(result, not(emptyList<Report>()))
        assertThat(result, IsEqual(listOf(report)))
    }
}