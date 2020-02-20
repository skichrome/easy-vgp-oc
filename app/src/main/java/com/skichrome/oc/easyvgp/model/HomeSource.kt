package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany

interface HomeSource
{
    suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>>
    suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long>
    suspend fun updateNewUserAndCompany(userAndCompany: UserAndCompany): Results<Int>
}