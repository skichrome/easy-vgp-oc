package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ControlPointDataDaoTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var db: AppDatabase
    private lateinit var controlPointDao: ControlPointDao
    private lateinit var controlPointDataDao: ControlPointDataDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        controlPointDao = db.controlPointDao()
        controlPointDataDao = db.controlPointDataDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    @Test
    @Throws(Exception::class)
    fun insertControlPointData_ControlPointDataDaoShouldInheritFromBaseDao() = runBlocking {
        val ctrlPtData = AndroidDataProvider.ctrlPointData2
        val ctrlPt = AndroidDataProvider.ctrlPointList.find { ctrlPtData.ctrlPointRef == it.id }

        assertThat(ctrlPt, IsNot(nullValue()))
        controlPointDao.insertReplace(ctrlPt!!)
        val result = controlPointDataDao.insertReplace(ctrlPtData)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, `is`(ctrlPtData.id))
    }
}