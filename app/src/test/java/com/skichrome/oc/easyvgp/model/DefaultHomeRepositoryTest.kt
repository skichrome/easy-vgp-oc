package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeHomeDataSource
import com.skichrome.oc.easyvgp.model.source.FakeNetManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultHomeRepositoryTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var netManager: FakeNetManager
    private lateinit var homeLocalSource: FakeHomeDataSource
    private lateinit var homeRemoteSource: FakeHomeDataSource
    private lateinit var repository: DefaultHomeRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        netManager = FakeNetManager(false)
        homeLocalSource = FakeHomeDataSource(
            userAndCompanyDataService = DataProvider.userCompanyHashMap
        )
        homeRemoteSource = FakeHomeDataSource()
        repository = DefaultHomeRepository(netManager = netManager, localSource = homeLocalSource, remoteSource = homeRemoteSource)
    }

    // --- Tests --- //

    @Test
    fun getAllUserAndCompany() = runBlockingTest {
        val result = repository.getAllUserAndCompany()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data.size, IsEqual(DataProvider.userCompanyList.size))
        assertThat(result.data, IsEqual(DataProvider.userCompanyList))
    }

    @Test
    fun insertNewUserAndCompany() = runBlockingTest {
        val userCompanyToInsert = UserAndCompany(company = DataProvider.companyToInsert, user = DataProvider.usertoInsert)
        val result = repository.insertNewUserAndCompany(userCompanyToInsert)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, instanceOf(Long::class.java))
        assertThat(result.data, `is`(userCompanyToInsert.company.id))
    }

    @Test
    fun updateNewUserAndCompany() = runBlockingTest {
        val userCompanyToUpdate = UserAndCompany(company = DataProvider.company1Edit, user = DataProvider.user1Edit)
        val result = repository.updateNewUserAndCompany(userCompanyToUpdate)

        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, instanceOf(Int::class.java))
        assertThat(result.data, `is`(1))
    }
}