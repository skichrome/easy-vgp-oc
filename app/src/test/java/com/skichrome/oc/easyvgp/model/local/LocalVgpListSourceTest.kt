package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
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
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class LocalVgpListSourceTest
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

    private lateinit var localSource: LocalVgpListSource

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
        machineDao = db.machinesDao()
        machineTypeDao = db.machinesTypeDao()
        controlPointDao = db.controlPointDao()
        controlPointDataDao = db.controlPointDataDao()
        machineControlPointDataDao = db.machineControlPointDataDao()
        machineControlPointDataExtraDao = db.machineControlPointDataExtraDao()
        customerDao = db.customersDao()

        localSource = LocalVgpListSource(
            machineCtrlPtExtraDao = machineControlPointDataExtraDao,
            customerDao = customerDao,
            machineControlPointDataDao = machineControlPointDataDao,
            dispatchers = Dispatchers.Main
        )
    }

    @After
    fun tearDown() = db.close()

    // --- Tests --- //

    @Test
    fun observeReports() = runBlocking {
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
        controlPointDao.insertReplace(*ctrlPtList.toTypedArray())
        machineTypeDao.insertReplace(*machineTypeList.toTypedArray())
        machineDao.insertReplace(*machineList.toTypedArray())
        controlPointDataDao.insertReplace(*ctrlPtData.toTypedArray())
        machineControlPointDataExtraDao.insertReplace(*extraList.toTypedArray())

        machineControlPointDataDao.insertIgnore(*ctrlPtDataList.toTypedArray())

        val result = localSource.observeReports().getOrAwaitValue()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(DataProvider.vgpListItemList))
    }

    @Test
    fun getMachineCtrlPtExtraFromId() = runBlocking {
        val extras = DataProvider.extraList
        val extra = DataProvider.extra3

        machineControlPointDataExtraDao.insertReplace(*extras.toTypedArray())

        val result = localSource.getMachineCtrlPtExtraFromId(extra.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(extra))
    }

    @Test
    fun updateMachineCtrlPtExtra() = runBlocking {
        val extra = DataProvider.extra1
        val extraEdit = DataProvider.extra1Edit

        assertThat(extra.id, `is`(extraEdit.id))

        machineControlPointDataExtraDao.insertReplace(extra)

        val result = localSource.updateMachineCtrlPtExtra(extraEdit)
        val dbResult = machineControlPointDataExtraDao.getExtraFromId(extraEdit.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
        assertThat(dbResult, IsEqual(extraEdit))
    }

    @Test
    fun loadCustomerEmail() = runBlocking {
        val customers = DataProvider.customerList
        val customer = DataProvider.customer2

        customerDao.insertReplace(*customers.toTypedArray())

        val result = localSource.loadCustomerEmail(customer.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(customer.email))
    }

    @Test
    fun downloadReportFromStorage() = runBlocking {
        val result = localSource.downloadReportFromStorage(null, File("/"))

        assertThat(result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NotImplementedException::class.java))
    }
}