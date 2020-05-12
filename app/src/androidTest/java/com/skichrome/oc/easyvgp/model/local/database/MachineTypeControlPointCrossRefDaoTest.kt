package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MachineTypeControlPointCrossRefDaoTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var db: AppDatabase
    private lateinit var machineTypeControlPointCrossRefDao: MachineTypeControlPointCrossRefDao
    private lateinit var machineTypeDao: MachineTypeDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        machineTypeControlPointCrossRefDao = db.machineTypeControlPointCrossRefDao()
        machineTypeDao = db.machinesTypeDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    // --- Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertMachineTypeControlPointCrossRef_machineTypeControlPointCrossRefDaoShouldInheritFromBaseDao() = runBlocking {
        val machineTypeWithCtrlPoints = AndroidDataProvider.machineTypeWithControlPointList

        val resultIds = mutableListOf<Long>()
        val expectedResults = mutableListOf<Long>()

        machineTypeWithCtrlPoints.forEach {
            it.controlPoints.forEachIndexed { index, ctrlPt ->
                resultIds.add(
                    machineTypeControlPointCrossRefDao.insertReplace(
                        MachineTypeControlPointCrossRef(
                            machineTypeId = it.machineType.id,
                            ctrlPointId = ctrlPt.id
                        )
                    )
                )
                expectedResults.add(index.toLong())
            }
        }

        assertThat(resultIds, not(IsEqual(emptyList<Long>())))
        assertThat(resultIds.size, IsEqual(expectedResults.size))
    }

    @Test
    @Throws(Exception::class)
    fun deleteAll_insertItemsAndDeleteAll_shouldReturnEmptyList() = runBlocking {
        val machineTypeWithCtrlPoints = AndroidDataProvider.machineTypeWithControlPointList

        val insertResults = mutableListOf<Long>()
        machineTypeWithCtrlPoints.forEach {
            it.controlPoints.forEachIndexed { index, ctrlPt ->
                machineTypeControlPointCrossRefDao.insertReplace(
                    MachineTypeControlPointCrossRef(
                        machineTypeId = it.machineType.id,
                        ctrlPointId = ctrlPt.id
                    )
                )
                insertResults.add(index.toLong())
            }
        }

        val result = machineTypeControlPointCrossRefDao.deleteAll()
        assertThat(result, `is`(insertResults.size))
    }

    @Test
    @Throws(Exception::class)
    fun deleteMatchMachineTypeId_insertItemsAndDeleteById_shouldReturnOnlyOneTypeWithCtrlPt() = runBlocking {
        val machineTypeWithCtrlPoints = AndroidDataProvider.machineTypeWithControlPointList
        val machineTypeWithCtrlPoint1 = AndroidDataProvider.machineTypeWithControlPoint1

        machineTypeWithCtrlPoints.forEach {
            it.controlPoints.forEach { ctrlPt ->
                machineTypeControlPointCrossRefDao.insertReplace(
                    MachineTypeControlPointCrossRef(
                        machineTypeId = it.machineType.id,
                        ctrlPointId = ctrlPt.id
                    )
                )
            }
        }
        val deleteResult = machineTypeControlPointCrossRefDao.deleteMatchMachineTypeId(machineTypeWithCtrlPoint1.machineType.id)

        assertThat(deleteResult, `is`(machineTypeWithCtrlPoint1.controlPoints.size))
    }
}