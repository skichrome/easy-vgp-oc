package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.HomeSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.AppCoroutinesConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalHomeSource(
    private val companyDao: CompanyDao,
    private val userDao: UserDao,
    private val dispatchers: CoroutineDispatcher = AppCoroutinesConfiguration.ioDispatchers
) : HomeSource
{
    override fun observeUsers(): LiveData<Results<List<User>>> = userDao.observeUsers().map { Success(it) }
    override fun observeCompanies(): LiveData<Results<List<Company>>> = companyDao.observeCompanies().map { Success(it) }

    override suspend fun getUserByFirebaseUid(uid: String): Results<User> = withContext(dispatchers) {
        return@withContext try
        {
            userDao.getUserByUid(uid)?.let {
                return@let Success(it)
            } ?: Error(ItemNotFoundException("User doesn't exist in database"))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewUser(user: User): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            Success(userDao.insertReplace(user))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateUser(user: User): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            Success(userDao.update(user))
        } catch (e: Exception)
        {
            Error(e)
        }
    }
}