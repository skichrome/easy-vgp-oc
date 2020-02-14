package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.Company
import com.skichrome.oc.easyvgp.model.local.database.User

interface HomeRepository
{
    fun observeUsers(): LiveData<Results<List<User>>>
    fun observeCompanies(): LiveData<Results<List<Company>>>

    suspend fun getUserByFirebaseUid(uid: String): Results<User>

    suspend fun insertNewUser(user: User): Results<Long>
    suspend fun updateUser(user: User): Results<Int>
}