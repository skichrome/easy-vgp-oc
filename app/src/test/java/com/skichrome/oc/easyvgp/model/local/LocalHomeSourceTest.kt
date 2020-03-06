package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.model.HomeSource
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.local.database.CompanyDao
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.model.local.database.UserDao
import com.skichrome.oc.easyvgp.model.source.DataProvider
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

    private lateinit var database: AppDatabase
    private lateinit var companyDao: CompanyDao
    private lateinit var userDao: UserDao
    private lateinit var homeSource: HomeSource

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        companyDao = database.companiesDao()
        userDao = database.usersDao()
        homeSource = LocalHomeSource(companyDao = companyDao, userDao = userDao, dispatchers = Dispatchers.Main)
    }

    @After
    fun tearDown() = database.close()

    // --- Configuration --- //

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
        val userCompanyToUpdate = UserAndCompany(DataProvider.company1Edit, DataProvider.user1Edit)
        homeSource.updateUserAndCompany(userCompanyToUpdate)

        // Get data with database DAO
        val result = userDao.getAllUserAndCompanies()
            .firstOrNull { it.company.id == userCompanyToUpdate.company.id && it.user.id == userCompanyToUpdate.user.id }

        // Inserted objects must be equal to retrieved objects
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(userCompanyToUpdate))
    }
}