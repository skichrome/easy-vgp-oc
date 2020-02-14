package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "Users")
data class User(
    @ColumnInfo(name = "user_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_firebase_uid", index = true) val firebaseUid: String,
    @ColumnInfo(name = "user_display_name") val name: String,
    @ColumnInfo(name = "user_email") val email: String
)

@Dao
interface UserDao : BaseDao<User>
{
    @Query("SELECT * FROM Users")
    fun observeUsers(): LiveData<List<User>>

    @Query("SELECT * FROM Users WHERE user_firebase_uid = :uid")
    fun getUserByUid(uid: String): User?
}