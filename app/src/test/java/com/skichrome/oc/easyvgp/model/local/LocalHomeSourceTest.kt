package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.HomeSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.util.NotImplementedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class LocalHomeSourceTest
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
    private lateinit var ctrlPointDao: ControlPointDao
    private lateinit var ctrlPointDataDao: ControlPointDataDao
    private lateinit var machineDao: MachineDao
    private lateinit var machineTypeDao: MachineTypeDao
    private lateinit var machineTypeControlPointCrossRefDao: MachineTypeControlPointCrossRefDao
    private lateinit var machineCtrlPtDataDao: MachineControlPointDataDao
    private lateinit var machineCtrlPtDataExtraDao: MachineControlPointDataExtraDao
    private lateinit var homeSource: HomeSource

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        companyDao = db.companiesDao()
        userDao = db.usersDao()
        customerDao = db.customersDao()
        ctrlPointDao = db.controlPointDao()
        ctrlPointDataDao = db.controlPointDataDao()
        machineDao = db.machinesDao()
        machineTypeDao = db.machinesTypeDao()
        machineTypeControlPointCrossRefDao = db.machineTypeControlPointCrossRefDao()
        machineCtrlPtDataDao = db.machineControlPointDataDao()
        machineCtrlPtDataExtraDao = db.machineControlPointDataExtraDao()

        homeSource = LocalHomeSource(
            companyDao = companyDao,
            userDao = userDao,
            machineTypeDao = machineTypeDao,
            controlPointDao = ctrlPointDao,
            machineTypeControlPointCrossRefDao = machineTypeControlPointCrossRefDao,
            machineCtrlPtDataExtraDao = machineCtrlPtDataExtraDao,
            machineControlPointDataDao = machineCtrlPtDataDao,
            dispatchers = Dispatchers.Main
        )
    }

    @After
    fun tearDown() = db.close()

    // --- Tests --- //

    @Test
    fun observeHomeReportsEndValidityDate_returnEmptyList() = runBlocking {
        val result = homeSource.observeHomeReportsEndValidityDate().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(emptyList()))
    }

    @Test
    fun observeHomeReportsEndValidityDateTest_insertWithDb_ReturnReportList() = runBlocking {
        val userAndCompany = DataProvider.userCompanyList
        val ctrlPtDataList = DataProvider.machCtrlPtDataList
        val ctrlPtList = DataProvider.ctrlPointList
        val customerList = DataProvider.customerList
        val machineList = DataProvider.machineList
        val machineTypeList = DataProvider.machineTypeList
        val ctrlPtData = DataProvider.ctrlPointDataList
        val extraList = DataProvider.extraList

        companyDao.insertReplace(*userAndCompany.map { it.company }.toTypedArray())
        userDao.insertReplace(*userAndCompany.map { it.user }.toTypedArray())
        customerDao.insertReplace(*customerList.toTypedArray())
        ctrlPointDao.insertReplace(*ctrlPtList.toTypedArray())
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())
        machineDao.insertReplace(*machineList.toTypedArray())
        ctrlPointDataDao.insertReplace(*ctrlPtData.toTypedArray())
        machineCtrlPtDataExtraDao.insertReplace(*extraList.toTypedArray())

        machineCtrlPtDataDao.insertIgnore(*ctrlPtDataList.toTypedArray())

        val result = homeSource.observeHomeReportsEndValidityDate().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(DataProvider.homeEndValidityReportItemList))
    }

    @Test
    fun updateExtraEmailSentStatus_insertExtra_updateEmailReminder_CheckEmailIsUpdated() = runBlocking {
        val extrasList = DataProvider.extraList
        val extra = DataProvider.extra2

        machineCtrlPtDataExtraDao.insertReplace(*extrasList.toTypedArray())

        val result = homeSource.updateExtraEmailSentStatus(extra.id)
        val storedResult = machineCtrlPtDataExtraDao.getExtraFromId(extra.id)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))

        assertThat(storedResult, IsNot(nullValue()))
        assertThat(storedResult.isReminderEmailSent, IsEqual(true))
    }

    @Test
    fun getAllUserAndCompany() = runBlocking {
        // Insert data with database DAO
        val userCompanyToInsert = DataProvider.userCompanyList
        userCompanyToInsert.forEach {
            companyDao.insertReplace(it.company)
            userDao.insertReplace(it.user)
        }

        // Get data with source
        val result = homeSource.getAllUserAndCompany()

        // Inserted objects must be equal to retrieved objects
        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, `is`(userCompanyToInsert.size))
        assertThat(result.data, IsEqual(userCompanyToInsert))
    }

    @Test
    fun insertNewUserAndCompany() = runBlocking {
        // Insert data with source
        val userCompanyToInsert = DataProvider.userCompanyList
        userCompanyToInsert.forEach { homeSource.insertNewUserAndCompany(it) }

        // Get data with database DAO
        val result = userDao.getAllUserAndCompanies()

        // Inserted objects must be equal to retrieved objects
        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(userCompanyToInsert.size))
        assertThat(result, IsEqual(userCompanyToInsert))
    }

    @Test
    fun updateUserAndCompany() = runBlocking {
        // Insert data with source
        DataProvider.userCompanyList.forEach { homeSource.insertNewUserAndCompany(it) }

        // Update data with source
        val userCompanyToUpdate = UserAndCompany(company = DataProvider.company1Edit, user = DataProvider.user1Edit)
        homeSource.updateUserAndCompany(userCompanyToUpdate)

        // Get data with database DAO
        val result = userDao.getAllUserAndCompanies()
            .firstOrNull { it.company.id == userCompanyToUpdate.company.id && it.user.id == userCompanyToUpdate.user.id }

        // Inserted objects must be equal to retrieved objects
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(userCompanyToUpdate))
    }

    @Test
    fun insertControlPointsAsync() = runBlocking {
        val ctrlPts = DataProvider.ctrlPointList
        val insertResult = homeSource.insertControlPointsAsync(ctrlPts).await()

        assertThat(insertResult, instanceOf(Success::class.java))
        assertThat((insertResult as Success).data, IsEqual(ctrlPts.map { it.id }))

        val result = ctrlPointDao.observeControlPoints().getOrAwaitValue()
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(ctrlPts))
    }

    @Test
    fun insertMachineTypesAsync() = runBlocking {
        val machTypes = DataProvider.machineTypeList
        val insertResult = homeSource.insertMachineTypesAsync(machTypes).await()

        assertThat(insertResult, instanceOf(Success::class.java))
        assertThat((insertResult as Success).data, IsEqual(machTypes.map { it.id }))

        val result = machineTypeDao.observeMachineTypes().getOrAwaitValue()
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(machTypes))
    }

    @Test
    fun insertMachineTypesWithCtrlPoints() = runBlocking {
        val ctrlPts = DataProvider.ctrlPointList
        val machTypes = DataProvider.machineTypeList
        val machTypeCtrlPt = DataProvider.machineTypeUpdateWithControlPointList

        ctrlPointDao.insertReplace(*ctrlPts.toTypedArray())
        machineTypeDao.insertReplace(*machTypes.toTypedArray())

        homeSource.insertMachineTypesWithCtrlPoints(machTypeCtrlPt)

        val result = machineTypeDao.getMachineTypeWithControlPointsFromMachineTypeId(machTypes.first().id)
        assertThat(result, IsNot(nullValue()))
        assertThat(result.machineType, IsEqual(machTypes.first()))
        assertThat(result.controlPoints, IsEqual(listOf(DataProvider.ctrlPoint1, DataProvider.ctrlPoint2)))
    }

    @Test
    fun getAllControlPointsAsync() = runBlocking {

        val result = homeSource.getAllControlPointsAsync().await()
        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NotImplementedException::class.java))
    }

    @Test
    fun getAllMachineTypesAsync() = runBlocking {
        val result = homeSource.getAllMachineTypesAsync().await()
        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NotImplementedException::class.java))
    }

    @Test
    fun getAllMachineTypeCtrlPointsAsync() = runBlocking {
        val result = homeSource.getAllMachineTypeCtrlPointsAsync().await()
        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NotImplementedException::class.java))
    }
}