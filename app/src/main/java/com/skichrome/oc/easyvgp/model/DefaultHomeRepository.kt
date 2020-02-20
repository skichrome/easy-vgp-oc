package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany

class DefaultHomeRepository(private val localSource: HomeSource) : HomeRepository
{
    override suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>> = localSource.getAllUserAndCompany()
    override suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long> = localSource.insertNewUserAndCompany(userAndCompany)
    override suspend fun updateNewUserAndCompany(userAndCompany: UserAndCompany): Results<Int> = localSource.updateNewUserAndCompany(userAndCompany)
}