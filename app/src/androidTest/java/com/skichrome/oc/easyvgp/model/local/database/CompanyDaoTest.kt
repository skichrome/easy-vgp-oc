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
class CompanyDaoTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var db: AppDatabase
    private lateinit var companyDao: CompanyDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        companyDao = db.companiesDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    @Test
    @Throws(Exception::class)
    fun insertCompany_companyDaoShouldInheritFromBaseDao() = runBlocking {
        val company = AndroidDataProvider.company1

        val result = companyDao.insertReplace(company)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, `is`(company.id))
    }
}