package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "Users")
data class User(
    @ColumnInfo(name = "user_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_company_id", index = true) var companyId: Long,
    @ColumnInfo(name = "user_firebase_uid", index = true) val firebaseUid: String,
    @ColumnInfo(name = "user_display_name") val name: String,
    @ColumnInfo(name = "user_email") val email: String,
    @ColumnInfo(name = "user_approval") val approval: String?,
    @ColumnInfo(name = "user_vat_number") val vatNumber: String?
)

data class UserAndCompany(
    @Embedded val company: Company,
    @Relation(
        parentColumn = "company_id",
        entityColumn = "user_company_id"
    )
    val user: User
)

@Dao
interface UserDao : BaseDao<User>
{
    @Query("SELECT * FROM Users")
    fun observeUsers(): LiveData<List<User>>

    @Transaction
    @Query("SELECT * FROM Companies")
    suspend fun getAllUserAndCompanies(): List<UserAndCompany>
}