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
    @ColumnInfo(name = "report_date") val reportDate: Long,
    @ColumnInfo(name = "is_valid") val isValid: Boolean = false
)

data class VgpListItem(
    @ColumnInfo(name = "report_date") val reportDate: Long,
    @ColumnInfo(name = "is_valid") val isValid: Boolean,
    @ColumnInfo(name = "ctrl_point_data_id") val controlPointDataId: Long
)

data class MachineIdWithControlPointData(
    @ColumnInfo(name = "machine_id") val machineId: Long,
    @ColumnInfo(name = "ctrl_point_data_id") val ctrlPointDataId: Long,
    @ColumnInfo(name = "control_point_id") val ctrlPointId: Long,
    @ColumnInfo(name = "control_point_name") val ctrlPointName: String
)

data class Report(
    @ColumnInfo(name = "machine_id") val machineId: Long,
    @ColumnInfo(name = "report_date") val reportDate: Long,
    @ColumnInfo(name = "is_valid") val isValid: Boolean,
    @Embedded val ctrlPointData: ControlPointData,
    @Embedded val ctrlPoint: ControlPoint
)

@Dao
interface MachineControlPointDataDao : BaseDao<MachineControlPointData>
{
    @Query("SELECT MachinesControlPointsDataCrossRef.report_date, MachinesControlPointsDataCrossRef.ctrl_point_data_id, MachinesControlPointsDataCrossRef.is_valid FROM MachinesControlPointsDataCrossRef LEFT JOIN ControlPointsData WHERE MachinesControlPointsDataCrossRef.ctrl_point_data_id == ControlPointsData.ctrl_point_data_id AND MachinesControlPointsDataCrossRef.machine_id == :id")
    suspend fun getCtrlPointDataFromMachineId(id: Long): List<VgpListItem>

    @Query("SELECT * FROM MachinesControlPointsDataCrossRef JOIN ControlPointsData JOIN control_points WHERE MachinesControlPointsDataCrossRef.report_date == :reportDate AND MachinesControlPointsDataCrossRef.ctrl_point_data_id == ControlPointsData.ctrl_point_data_id AND ControlPointsData.ctrl_point_data_ctrl_point_ref == control_points.control_point_id")
    suspend fun getPreviouslyInsertedReport(reportDate: Long): List<Report>

    @Query("SELECT MachinesControlPointsDataCrossRef.machine_id, ControlPointsData.ctrl_point_data_id, control_points.control_point_id, control_points.control_point_name FROM MachinesControlPointsDataCrossRef JOIN ControlPointsData JOIN control_points WHERE MachinesControlPointsDataCrossRef.machine_id == :id AND MachinesControlPointsDataCrossRef.ctrl_point_data_id == ControlPointsData.ctrl_point_data_id AND control_points.control_point_id == ControlPointsData.ctrl_point_data_ctrl_point_ref")
    suspend fun getCtrlPointControlPointDataFromMachineId(id: Long): List<MachineIdWithControlPointData>
}