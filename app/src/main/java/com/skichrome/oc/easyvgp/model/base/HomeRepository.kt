package com.skichrome.oc.easyvgp.model.base

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany

interface HomeRepository
{
    suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>>
    suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long>
    suspend fun updateNewUserAndCompany(userAndCompany: UserAndCompany): Results<Int>

    suspend fun synchronizeDatabase(): Results<Boolean>
}