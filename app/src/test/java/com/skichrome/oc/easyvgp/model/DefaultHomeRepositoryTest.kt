package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeHomeDataSource
import com.skichrome.oc.easyvgp.model.source.FakeNetManager
import com.skichrome.oc.easyvgp.util.NetworkException
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

    @Test
    fun updateExtraEmailSentStatusTest() = runBlockingTest {
        val extras = DataProvider.extraList
        val extraToUpdate = DataProvider.extra2
        homeLocalSource.insertExtras(extras)

        val result = repository.updateExtraEmailSentStatus(extraToUpdate.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, `is`(1))

        val sourceResult = homeLocalSource.getExtra(extraToUpdate.id)

        assertThat(sourceResult, instanceOf(Success::class.java))
        assertThat((sourceResult as Success).data.isReminderEmailSent, `is`(true))
    }

    @Test
    fun synchronizeDatabase_offlineMode_shouldThrowAnError_InternetAccessMustBeAvailable() = runBlockingTest {
        val result = repository.synchronizeDatabase()
        assertThat(result, instanceOf(Results.Error::class.java))
        assertThat((result as Results.Error).exception, instanceOf(NetworkException::class.java))
    }

// Todo Will be added when I will find a good way to test methods that use async coroutines

//    @Test
//    fun synchronizeDatabase_onlineMode_homeAndRemoteSourcesShouldContainSameData() = runBlockingTest {
//        val ctrlPts = DataProvider.ctrlPointHashMap
//        val machTypes = DataProvider.machineTypeHashMap
//        val machTypeCtrlPt = DataProvider.machineTypeCtrlPointCrossRefList
//
//        homeRemoteSource.insertData(ctrlPt = ctrlPts, machTypes = machTypes, machTypesCtrlPt = machTypeCtrlPt)
//
//        netManager.setIsFakeConnected(true)
//        val result = repositoryTest()//.getOrAwaitValue()
//
////        assertThat(result, instanceOf(Success::class.java))
////        assertThat((result as Success).data, `is`(true))
//
//        val ctrlPtResult = homeLocalSource.getAllControlPointsAsync()
//    }
//
//    private fun CoroutineScope.repositoryTest(): LiveData<Results<Boolean>>
//    {
//        val result = MutableLiveData<Results<Boolean>>()
//        launch {
//            result.value = repository.synchronizeDatabase()
//        }
//        return result
//    }
}