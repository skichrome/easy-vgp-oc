package com.skichrome.oc.easyvgp.model

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeNetManager
import com.skichrome.oc.easyvgp.model.source.FakeVgpListSource
import com.skichrome.oc.easyvgp.util.NetworkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DefaultVgpListRepositoryTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var netManager: FakeNetManager
    private lateinit var localSrc: FakeVgpListSource
    private lateinit var remoteSrc: FakeVgpListSource
    private lateinit var repository: DefaultVgpListRepository

    // =================================
    //              Methods
    // =================================

    @Before
    fun setUp()
    {
        netManager = FakeNetManager(false)
        localSrc = FakeVgpListSource(extrasDataService = DataProvider.extraMap, customersDataService = DataProvider.localCustomerMap)
        remoteSrc = FakeVgpListSource()

        repository = DefaultVgpListRepository(netManager = netManager, localSource = localSrc, remoteSource = remoteSrc)

        localSrc.refresh()
    }

    @Test
    fun observeReports_shouldReturnReportList() = runBlockingTest {
        val result = repository.observeReports().getOrAwaitValue()
        val expectedResult = localSrc.getVgpListItems()

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(expectedResult))
    }

    @Test
    fun downloadReportFromStorage() = runBlockingTest {
        val result = repository.downloadReportFromStorage(0L, "", File("/"))
        assertThat("By default if no internet connexion is available the repository should return an error", result, instanceOf(Error::class.java))
        assertThat((result as Error).exception, instanceOf(NetworkException::class.java))
    }

    @Test
    fun loadCustomerEmail() = runBlockingTest {
        val customer = DataProvider.customer1

        val result = repository.loadCustomerEmail(customerId = customer.id)

        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(customer.email))
    }
}