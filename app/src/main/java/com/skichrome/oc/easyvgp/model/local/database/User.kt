package com.skichrome.oc.easyvgp.model.local.database

import android.net.Uri
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
    @ColumnInfo(name = "user_vat_number") val vatNumber: String?,
    @ColumnInfo(name = "user_signature_picture_path") val signaturePath: Uri? = null,
    @ColumnInfo(name = "user_signature_picture_remote_path") var remoteSignaturePath: Uri? = null,
    @ColumnInfo(name = "user_signature_in_report_enabled") val isSignatureEnabled: Boolean = false
)

data class UserAndCompany(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_company_id",
        entityColumn = "company_id"
    )
    val company: Company
)

@Dao
interface UserDao : BaseDao<User>
{
    @Query("SELECT * FROM Users")
    fun observeUsers(): LiveData<List<User>>

    @Transaction
    @Query("SELECT * FROM Users WHERE user_id = :userId")
    suspend fun getUserFromId(userId: Long): UserAndCompany

    @Transaction
    @Query("SELECT * FROM Users")
    suspend fun getAllUserAndCompanies(): List<UserAndCompany>
}