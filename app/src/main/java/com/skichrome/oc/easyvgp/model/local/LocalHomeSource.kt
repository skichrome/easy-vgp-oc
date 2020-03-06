package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.HomeSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.CompanyDao
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.model.local.database.UserDao
import com.skichrome.oc.easyvgp.util.AppCoroutinesConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalHomeSource(
    private val companyDao: CompanyDao,
    private val userDao: UserDao,
    private val dispatchers: CoroutineDispatcher = AppCoroutinesConfiguration.ioDispatchers
) : HomeSource
{
    override suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>> = withContext(dispatchers) {
        return@withContext try
        {
            Success(userDao.getAllUserAndCompanies())
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            userAndCompany.user.companyId = companyDao.insertReplace(userAndCompany.company)
            val result = userDao.insertReplace(userAndCompany.user)
            Success(result)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateUserAndCompany(userAndCompany: UserAndCompany): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            companyDao.update(userAndCompany.company)
            val result = userDao.update(userAndCompany.user)
            Success(result)
        } catch (e: Exception)
        {
            Error(e)
        }
    }
}