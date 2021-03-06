package com.skichrome.oc.easyvgp.model.local.database

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(
    tableName = "Machines",
    foreignKeys = [ForeignKey(
        entity = MachineType::class,
        parentColumns = ["machine_type_id"],
        childColumns = ["machine_type_ref"]
    ),
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["customer_id"],
            childColumns = ["customer_ref"]
        )]
)
data class Machine(
    @ColumnInfo(name = "machine_id") @PrimaryKey(autoGenerate = true) val machineId: Long,
    @ColumnInfo(name = "machine_name") val name: String,
    @ColumnInfo(name = "machine_park_number") val parkNumber: String,
    @ColumnInfo(name = "machine_serial") val serial: String,
    @ColumnInfo(name = "machine_brand") val brand: String,
    @ColumnInfo(name = "machine_model") val model: String,
    @ColumnInfo(name = "machine_manufacturing_year") val manufacturingYear: Int,
    @ColumnInfo(name = "machine_local_photo_reference") val localPhotoRef: String?,
    @ColumnInfo(name = "machine_remote_photo_reference") var remotePhotoRef: Uri? = null,
    @ColumnInfo(name = "customer_ref", index = true) val customer: Long,
    @ColumnInfo(name = "machine_type_ref", index = true) val type: Long
)

@Dao
interface MachineDao : BaseDao<Machine>
{
    @Query("SELECT * FROM Machines")
    fun observeMachines(): LiveData<List<Machine>>

    @Query("SELECT * FROM Machines WHERE machine_id = :machineId")
    suspend fun getMachineById(machineId: Long): Machine
}