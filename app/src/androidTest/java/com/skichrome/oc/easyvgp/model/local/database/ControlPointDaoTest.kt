package com.skichrome.oc.easyvgp.model.local.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValueFromAndroidTests
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
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
class ControlPointDaoTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var controlPointDao: ControlPointDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        controlPointDao = db.controlPointDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    // --- Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertControlPt_controlPointDaoShouldInheritFromBaseDao() = runBlocking {
        val controlPoint = AndroidDataProvider.ctrlPoint1
        val result = controlPointDao.insertReplace(controlPoint)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, `is`(controlPoint.id))
    }

    @Test
    @Throws(Exception::class)
    fun observeControlPoints_shouldReturnEmptyList() = runBlocking {
        val result = controlPointDao.observeControlPoints().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(listOf()))
    }

    @Test
    @Throws(Exception::class)
    fun observeControlPoints_shouldReturnCtrlPointList() = runBlocking {
        val ctrlPoints = AndroidDataProvider.ctrlPointList

        controlPointDao.insertReplace(*ctrlPoints.toTypedArray())

        val result = controlPointDao.observeControlPoints().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(ctrlPoints))
    }
}