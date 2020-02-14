package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.Company
import com.skichrome.oc.easyvgp.model.local.database.User

class DefaultHomeRepository(private val localSource: HomeSource) : HomeRepository
{
    override fun observeUsers(): LiveData<Results<List<User>>> = localSource.observeUsers()

    override fun observeCompanies(): LiveData<Results<List<Company>>> = localSource.observeCompanies()

    override suspend fun getUserByFirebaseUid(uid: String): Results<User> = localSource.getUserByFirebaseUid(uid)

    override suspend fun insertNewUser(user: User): Results<Long> = localSource.insertNewUser(user)

    override suspend fun updateUser(user: User): Results<Int> = localSource.updateUser(user)
}