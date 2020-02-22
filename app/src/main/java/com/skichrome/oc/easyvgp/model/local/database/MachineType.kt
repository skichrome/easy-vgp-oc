package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class MachineType(
    @ColumnInfo(name = "machine_type_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "machine_type_remote_id", index = true) val remoteId: String,
    @ColumnInfo(name = "machine_legal_name", index = true) val legalName: String,
    @ColumnInfo(name = "machine_type_name") val name: String
)

@Dao
interface MachineTypeDao : BaseDao<MachineType>
{
    @Query("SELECT * FROM MachineType")
    fun observeMachineTypes(): LiveData<List<MachineType>>
}