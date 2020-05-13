package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.*
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType

@Entity(
    tableName = "ControlPointsData",
    foreignKeys = [
        ForeignKey(
            entity = ControlPoint::class,
            parentColumns = ["control_point_id"],
            childColumns = ["ctrl_point_data_ctrl_point_ref"]
        )
    ]
)
data class ControlPointData(
    @ColumnInfo(name = "ctrl_point_data_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "ctrl_point_data_ctrl_point_ref", index = true) val ctrlPointRef: Long,
    @ColumnInfo(name = "ctrl_point_data_choice_possibility_index", index = true) val ctrlPointPossibility: ChoicePossibility,
    @ColumnInfo(name = "ctrl_point_data_verification_type_index", index = true) val ctrlPointVerificationType: VerificationType,
    @ColumnInfo(name = "ctrl_point_data_comment") val comment: String?
)

@Dao
interface ControlPointDataDao : BaseDao<ControlPointData>