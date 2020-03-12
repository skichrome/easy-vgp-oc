package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "MachinesControlPointsDataCrossRef",
    primaryKeys = ["machine_id", "ctrl_point_data_id"],
    foreignKeys = [
        ForeignKey(
            entity = Machine::class,
            parentColumns = ["machine_id"],
            childColumns = ["machine_id"]
        ),
        ForeignKey(
            entity = ControlPointData::class,
            parentColumns = ["ctrl_point_data_id"],
            childColumns = ["ctrl_point_data_id"]
        )
    ]
)
data class MachineControlPointData(
    @ColumnInfo(name = "machine_id", index = true) val machineId: Long,
    @ColumnInfo(name = "ctrl_point_data_id", index = true) val ctrlPointDataId: Long,
    @ColumnInfo(name = "report_date") val reportDate: Long
)

@Dao
interface MachineControlPointDataDao : BaseDao<MachineControlPointData>