package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpSetupSource
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtraDao
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
class LocalNewVgpSetupSourceTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var machineCtrlPtDataExtraDao: MachineControlPointDataExtraDao

    private lateinit var localNewVgpSetupSource: NewVgpSetupSource

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

        machineCtrlPtDataExtraDao = db.machineControlPointDataExtraDao()

        localNewVgpSetupSource = LocalNewVgpSetupSource(
            machineCtrlPtExtraDao = machineCtrlPtDataExtraDao,
            dispatcher = Dispatchers.Main
        )
    }

    @After
    fun tearDown() = db.close()

    // --- Tests --- //

    @Test
    fun getPreviousCtrlPtDataExtraFromDateTest_insertWithDb_returnReportExtraFromDate() = runBlocking {
        val extras = DataProvider.extraList
        val expected = DataProvider.extra2

        machineCtrlPtDataExtraDao.insertReplace(*extras.toTypedArray())

        val result = localNewVgpSetupSource.getPreviousCtrlPtDataExtraFromDate(expected.reportDate)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(expected))
    }

    @Test
    fun insertMachineCtrlPtDataExtraTest_insertExtra_CheckExtraIsInserted() = runBlocking {
        val extra = DataProvider.extra1

        val result = localNewVgpSetupSource.insertMachineCtrlPtDataExtra(extra)
        val dbResult = machineCtrlPtDataExtraDao.getExtraFromId(extra.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(extra.id))
        assertThat(extra, IsEqual(dbResult))
    }

    @Test
    fun updateMachineCtrlPtDataExtraTest_insertAndUpdateExtra_checkExtraIsUpdated() = runBlocking {
        val extra = DataProvider.extra1
        val extraEdit = DataProvider.extra1Edit

        assertThat(extra.id, `is`(extraEdit.id))

        localNewVgpSetupSource.insertMachineCtrlPtDataExtra(extra)
        val result = localNewVgpSetupSource.updateMachineCtrlPtDataExtra(extraEdit)
        val dbResult = machineCtrlPtDataExtraDao.getExtraFromId(extraEdit.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
        assertThat(extraEdit, IsEqual(dbResult))
    }

    @Test
    fun deleteMachineCtrlPtDataExtraTest_insertAndDeleteExtra_checkExtraIsDeleted() = runBlocking {
        val extras = DataProvider.extraList
        val deleted = DataProvider.extra1

        machineCtrlPtDataExtraDao.insertReplace(*extras.toTypedArray())

        val result = localNewVgpSetupSource.deleteMachineCtrlPtDataExtra(deleted.reportDate)
        val dbResult = machineCtrlPtDataExtraDao.getExtraFromId(deleted.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))
        assertThat(dbResult, `is`(nullValue()))
    }
}