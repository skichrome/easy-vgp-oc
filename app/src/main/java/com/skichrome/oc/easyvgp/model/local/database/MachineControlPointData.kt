package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.*

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

data class MachineIdWithControlPointData(
    @ColumnInfo(name = "machine_id") val machineId: Long,
    @ColumnInfo(name = "ctrl_point_data_id") val ctrlPointDataId: Long,
    @ColumnInfo(name = "control_point_id") val ctrlPointId: Long,
    @ColumnInfo(name = "control_point_name") val ctrlPointName: String
)

@Dao
interface MachineControlPointDataDao : BaseDao<MachineControlPointData>
{
    @Query("SELECT MachinesControlPointsDataCrossRef.report_date FROM MachinesControlPointsDataCrossRef LEFT JOIN ControlPointsData WHERE MachinesControlPointsDataCrossRef.ctrl_point_data_id == ControlPointsData.ctrl_point_data_id AND MachinesControlPointsDataCrossRef.machine_id == :id")
    suspend fun getCtrlPointDataFromMachineId(id: Long): List<Long>

    @Query("SELECT MachinesControlPointsDataCrossRef.machine_id, ControlPointsData.ctrl_point_data_id, control_points.control_point_id, control_points.control_point_name FROM MachinesControlPointsDataCrossRef JOIN ControlPointsData JOIN control_points WHERE MachinesControlPointsDataCrossRef.machine_id == :id AND MachinesControlPointsDataCrossRef.ctrl_point_data_id == ControlPointsData.ctrl_point_data_id AND control_points.control_point_id == ControlPointsData.ctrl_point_data_ctrl_point_ref")
    suspend fun getCtrlPointControlPointDataFromMachineId(id: Long): List<MachineIdWithControlPointData>
}