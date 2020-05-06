package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MachineControlPointDataExtraDaoTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var db: AppDatabase
    private lateinit var machineControlPointDataExtraDao: MachineControlPointDataExtraDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        machineControlPointDataExtraDao = db.machineControlPointDataExtraDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    // --- Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertCompany_companyDaoShouldInheritFromBaseDao() = runBlocking {
        val extra = AndroidDataProvider.extra3

        val result = machineControlPointDataExtraDao.insertReplace(extra)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, `is`(extra.id))
    }

    @Test
    @Throws(Exception::class)
    fun getPreviouslyCreatedExtras_insertExtraList_getExtraByDate_shouldReturnOneExtra() = runBlocking {
        val extras = AndroidDataProvider.extraList
        val extra = AndroidDataProvider.extra3

        machineControlPointDataExtraDao.insertReplace(*extras.toTypedArray())

        val result = machineControlPointDataExtraDao.getPreviouslyCreatedExtras(extra.reportDate)
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(extra))
    }

    @Test
    @Throws(Exception::class)
    fun getExtraFromId_insertExtraList_getExtraById_shouldReturnOneExtra() = runBlocking {
        val extras = AndroidDataProvider.extraList
        val extra = AndroidDataProvider.extra2

        machineControlPointDataExtraDao.insertReplace(*extras.toTypedArray())

        val result = machineControlPointDataExtraDao.getExtraFromId(extra.id)
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(extra))
    }

    @Test
    @Throws(Exception::class)
    fun updateExtraEmailStatus_insertExtraList_UpdateExtraEmail_shouldReturnExtraUpdated() = runBlocking {
        val extras = AndroidDataProvider.extraList
        val extra = AndroidDataProvider.extra2

        assertThat(extra.isReminderEmailSent, `is`(false))
        machineControlPointDataExtraDao.insertReplace(*extras.toTypedArray())

        val updateResult = machineControlPointDataExtraDao.updateExtraEmailStatus(extra.id)
        assertThat(updateResult, `is`(1))

        val result = machineControlPointDataExtraDao.getExtraFromId(extra.id)
        assertThat(result, IsNot(nullValue()))
        assertThat(result.isReminderEmailSent, `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun updateControlResult_insertExtraList_UpdateControlResult_shouldReturnExtraUpdated() = runBlocking {
        val extras = AndroidDataProvider.extraList
        val extra = AndroidDataProvider.extra2

        assertThat(extra.controlGlobalResult, `is`(ControlResult.RESULT_OK))
        machineControlPointDataExtraDao.insertReplace(*extras.toTypedArray())

        val updateResult = machineControlPointDataExtraDao.updateControlResult(extra.id, ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED)
        assertThat(updateResult, `is`(1))

        val result = machineControlPointDataExtraDao.getExtraFromId(extra.id)
        assertThat(result, IsNot(nullValue()))
        assertThat(result.controlGlobalResult, IsEqual(ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED))
    }

    @Test
    @Throws(Exception::class)
    fun deleteMatchMachineTypeId_insertItemsAndDeleteById_shouldReturnOnlyOneTypeWithCtrlPt() = runBlocking {
        val extras = AndroidDataProvider.extraList
        val extra = AndroidDataProvider.extra1

        machineControlPointDataExtraDao.insertReplace(*extras.toTypedArray())

        val deleteResult = machineControlPointDataExtraDao.deleteExtraFromDate(extra.reportDate)
        val expectedNullResult = machineControlPointDataExtraDao.getExtraFromId(extra.id)

        assertThat(deleteResult, `is`(1))
        assertThat(expectedNullResult, `is`(nullValue()))
    }
}