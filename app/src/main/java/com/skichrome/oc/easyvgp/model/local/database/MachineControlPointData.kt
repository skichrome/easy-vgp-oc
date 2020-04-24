package com.skichrome.oc.easyvgp.model.local.database

import android.net.Uri
import androidx.lifecycle.LiveData
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
        ),
        ForeignKey(
            entity = MachineControlPointDataExtra::class,
            parentColumns = ["machines_control_points_data_extras_id"],
            childColumns = ["machine_ctrl_point_data_extras_reference"]
        )
    ]
)
data class MachineControlPointData(
    @ColumnInfo(name = "machine_id", index = true) val machineId: Long,
    @ColumnInfo(name = "ctrl_point_data_id", index = true) val ctrlPointDataId: Long,
    @ColumnInfo(name = "machine_ctrl_point_data_extras_reference", index = true) val machineCtrlPointDataExtra: Long
)

data class VgpListItem(
    @ColumnInfo(name = "machine_id") val machineId: Long,
    @ColumnInfo(name = "report_date") val reportDate: Long,
    @ColumnInfo(name = "report_end_validity_date") val reportEndDate: Long,
    @ColumnInfo(name = "is_report_valid") val isValid: Boolean,
    @ColumnInfo(name = "report_path_on_device") val reportLocalPath: String?,
    @ColumnInfo(name = "report_path_on_remote_storage") val reportRemotePath: String?,
    @ColumnInfo(name = "ctrl_point_data_id") val controlPointDataId: Long,
    @ColumnInfo(name = "machines_control_points_data_extras_id") val extrasReference: Long
)

data class HomeEndValidityReportItem(
    @ColumnInfo(name = "machines_control_points_data_extras_id") val id: Long,
    @ColumnInfo(name = "report_end_validity_date") val reportEndDate: Long,
    @ColumnInfo(name = "is_report_valid") val isValid: Boolean,
    @ColumnInfo(name = "machine_local_photo_reference") val localPicture: String?,
    @ColumnInfo(name = "machine_remote_photo_reference") val remotePicture: Uri?,
    @ColumnInfo(name = "machine_name") val machineName: String,
    @ColumnInfo(name = "company_name") val companyName: String,
    @ColumnInfo(name = "email") val customerEmail: String,
    @ColumnInfo(name = "report_path_on_device") val reportLocalPath: String?
)
{
    @Ignore
    var reportDeltaDay: Long? = null
}

data class Report(
    @ColumnInfo(name = "machine_id") val machineId: Long,
    @Embedded val ctrlPointData: ControlPointData,
    @Embedded val ctrlPoint: ControlPoint,
    @Embedded val ctrlPointDataExtra: MachineControlPointDataExtra
)

@Dao
interface MachineControlPointDataDao : BaseDao<MachineControlPointData>
{
    @Query(
        "SELECT" +
                " MachinesControlPointsDataExtras.report_date," +
                " MachinesControlPointsDataExtras.report_end_validity_date," +
                " MachinesControlPointsDataExtras.report_path_on_device," +
                " MachinesControlPointsDataExtras.report_path_on_remote_storage," +
                " MachinesControlPointsDataExtras.machines_control_points_data_extras_id," +
                " MachinesControlPointsDataCrossRef.ctrl_point_data_id," +
                " MachinesControlPointsDataCrossRef.machine_id," +
                " MachinesControlPointsDataExtras.is_report_valid " +
                "FROM MachinesControlPointsDataCrossRef " +
                "LEFT JOIN ControlPointsData" +
                " JOIN MachinesControlPointsDataExtras " +
                "WHERE MachinesControlPointsDataCrossRef.ctrl_point_data_id == ControlPointsData.ctrl_point_data_id" +
                " AND MachinesControlPointsDataExtras.machines_control_points_data_extras_id == MachinesControlPointsDataCrossRef.machine_ctrl_point_data_extras_reference"
    )
    fun observeCtrlPtData(): LiveData<List<VgpListItem>>

    @Query(
        "SELECT" +
                " MachinesControlPointsDataExtras.machines_control_points_data_extras_id," +
                " MachinesControlPointsDataExtras.report_path_on_device," +
                " MachinesControlPointsDataExtras.is_report_valid," +
                " MachinesControlPointsDataExtras.report_end_validity_date," +
                " Machines.machine_name, Machines.machine_local_photo_reference," +
                " Machines.machine_remote_photo_reference," +
                " Customers.company_name," +
                " Customers.email " +
                "FROM MachinesControlPointsDataCrossRef " +
                "JOIN" +
                " MachinesControlPointsDataExtras" +
                " JOIN Machines" +
                " JOIN Customers " +
                "WHERE " +
                "MachinesControlPointsDataCrossRef.machine_ctrl_point_data_extras_reference == MachinesControlPointsDataExtras.machines_control_points_data_extras_id" +
                " AND MachinesControlPointsDataCrossRef.machine_id == Machines.machine_id" +
                " AND Machines.customer_ref == Customers.customer_id"
    )
    fun observeEndValidityReports(): LiveData<List<HomeEndValidityReportItem>>

    @Query(
        "SELECT * FROM MachinesControlPointsDataCrossRef " +
                "JOIN ControlPointsData" +
                " JOIN control_points " +
                "JOIN MachinesControlPointsDataExtras " +
                "WHERE MachinesControlPointsDataExtras.report_date == :reportDate" +
                " AND MachinesControlPointsDataCrossRef.ctrl_point_data_id == ControlPointsData.ctrl_point_data_id" +
                " AND ControlPointsData.ctrl_point_data_ctrl_point_ref == control_points.control_point_id" +
                " AND MachinesControlPointsDataExtras.machines_control_points_data_extras_id == MachinesControlPointsDataCrossRef.machine_ctrl_point_data_extras_reference"
    )
    suspend fun getPreviouslyInsertedReport(reportDate: Long): List<Report>
}