package com.skichrome.oc.easyvgp.model.source

import com.skichrome.oc.easyvgp.model.HomeSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeHomeDataSource(private val userAndCompanyDataService: LinkedHashMap<Long, UserAndCompany> = LinkedHashMap()) : HomeSource
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>> = Success(userAndCompanyDataService.values.toList())

    override suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long>
    {
        userAndCompanyDataService[userAndCompany.company.id] = userAndCompany
        return Success(userAndCompany.company.id)
    }

    override suspend fun updateUserAndCompany(userAndCompany: UserAndCompany): Results<Int>
    {
        val userExist = userAndCompanyDataService[userAndCompany.company.id]
        return if (userExist != null)
        {
            userAndCompanyDataService[userAndCompany.company.id] = userAndCompany
            Success(1)
        } else
            Error(ItemNotFoundException("Item doesnt' exist in the list"))
    }
}