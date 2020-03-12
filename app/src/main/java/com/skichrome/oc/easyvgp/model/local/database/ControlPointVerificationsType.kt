package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "ControlPointVerificationsTypes")
data class ControlPointVerificationType(
    @ColumnInfo(name = "ctrl_point_verification_type_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "ctrl_point_verification_type_name") val name: String
)

@Dao
interface ControlPointVerificationTypeDao
{
    @Query("SELECT * FROM ControlPointVerificationsTypes")
    fun observeVerificationsType(): LiveData<List<ControlPointVerificationType>>
}