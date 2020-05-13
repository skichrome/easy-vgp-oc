package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.*
import com.skichrome.oc.easyvgp.model.local.ControlType

@Entity(
    tableName = "MachinesControlPointsDataExtras",
    indices = [Index(unique = true, name = "report_date_idx", value = ["report_date"])]
)
data class MachineControlPointDataExtra(
    @ColumnInfo(name = "machines_control_points_data_extras_id") @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "report_date") val reportDate: Long,
    @ColumnInfo(name = "report_end_validity_date") val reportEndDate: Long,
    @ColumnInfo(name = "is_report_valid") var isValid: Boolean = false,
    @ColumnInfo(name = "reminder_email_sent") val isReminderEmailSent: Boolean = false,
    @ColumnInfo(name = "report_valid_email_sent") val isReportValidEmailSent: Boolean = false,
    @ColumnInfo(name = "report_path_on_device") var reportLocalPath: String? = null,
    @ColumnInfo(name = "report_path_on_remote_storage") var reportRemotePath: String? = null,
    @ColumnInfo(name = "is_machine_clean_for_control") val isMachineClean: Boolean,
    @ColumnInfo(name = "is_machine_ce_marked") val isMachineCE: Boolean,
    @ColumnInfo(name = "is_machine_notice_available") val machineNotice: Boolean,
    @ColumnInfo(name = "is_machine_equip_for_lifting") val isLiftingEquip: Boolean,
    @ColumnInfo(name = "is_tests_with_load") val isTestsWithLoad: Boolean,
    @ColumnInfo(name = "is_tests_with_nominal_load") val isTestsWithNominalLoad: Boolean?,
    @ColumnInfo(name = "is_tests_triggered_sensors") val testsHasTriggeredSensors: Boolean?,
    @ColumnInfo(name = "test_load_type") val loadType: String?,
    @ColumnInfo(name = "test_load_mass_in_kilos") val loadMass: Int?,
    @ColumnInfo(name = "control_type") val controlType: ControlType,
    @ColumnInfo(name = "control_general_result") val controlGlobalResult: ControlResult = ControlResult.RESULT_OK,
    @ColumnInfo(name = "machine_hours") val machineHours: Int,
    @ColumnInfo(name = "machine_intervention_place") val interventionPlace: String
)

@Dao
interface MachineControlPointDataExtraDao : BaseDao<MachineControlPointDataExtra>
{
    @Query("SELECT * FROM MachinesControlPointsDataExtras WHERE report_date == :date")
    suspend fun getPreviouslyCreatedExtras(date: Long): MachineControlPointDataExtra

    @Query("SELECT * FROM MachinesControlPointsDataExtras WHERE machines_control_points_data_extras_id == :id")
    suspend fun getExtraFromId(id: Long): MachineControlPointDataExtra

    @Query("UPDATE MachinesControlPointsDataExtras SET reminder_email_sent = 1 WHERE MachinesControlPointsDataExtras.machines_control_points_data_extras_id == :extraId")
    suspend fun updateExtraEmailStatus(extraId: Long): Int

    @Query("UPDATE MachinesControlPointsDataExtras SET control_general_result = :controlResult WHERE MachinesControlPointsDataExtras.machines_control_points_data_extras_id == :extraId")
    suspend fun updateControlResult(extraId: Long, controlResult: ControlResult): Int

    @Query("DELETE FROM MachinesControlPointsDataExtras WHERE MachinesControlPointsDataExtras.report_date == :date")
    suspend fun deleteExtraFromDate(date: Long): Int
}