package com.skichrome.oc.easyvgp.model.base

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.HomeEndValidityReportItem
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany

interface HomeRepository
{
    fun observeReports(): LiveData<Results<List<HomeEndValidityReportItem>>>

    suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>>
    suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long>
    suspend fun updateNewUserAndCompany(userAndCompany: UserAndCompany): Results<Int>
    suspend fun updateExtraEmailSentStatus(extraId: Long): Results<Int>

    suspend fun synchronizeDatabase(): Results<Boolean>
}