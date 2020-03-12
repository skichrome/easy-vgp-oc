package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.*

@Entity(
    tableName = "ControlPointsData",
    foreignKeys = [
        ForeignKey(
            entity = ControlPoint::class,
            parentColumns = ["control_point_id"],
            childColumns = ["ctrl_point_data_ctrl_point_ref"]
        ),
        ForeignKey(
            entity = ControlPointChoicePossibility::class,
            parentColumns = ["ctrl_point_choice_possibility_id"],
            childColumns = ["ctrl_point_data_choice_possibility_ref"]
        ),
        ForeignKey(
            entity = ControlPointVerificationType::class,
            parentColumns = ["ctrl_point_verification_type_id"],
            childColumns = ["ctrl_point_data_verification_type_ref"]
        )
    ]
)
data class ControlPointData(
    @ColumnInfo(name = "ctrl_point_data_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "ctrl_point_data_ctrl_point_ref", index = true) val ctrlPointRef: Long,
    @ColumnInfo(name = "ctrl_point_data_choice_possibility_ref", index = true) val ctrlPointPossibilityRef: Long,
    @ColumnInfo(name = "ctrl_point_data_verification_type_ref", index = true) val ctrlPointVerificationTypeRef: Long,
    @ColumnInfo(name = "ctrl_point_data_comment") val comment: String?
)

data class ControlPointDataVgpTest(
    @Embedded val controlPointData: ControlPointData,

    @Relation(
        parentColumn = "ctrl_point_data_id",
        entityColumn = "control_point_id"
    )
    val controlPoint: ControlPoint,
    @Relation(
        parentColumn = "ctrl_point_data_choice_possibility_ref",
        entityColumn = "ctrl_point_choice_possibility_id"
    )
    val choicePossibility: ControlPointChoicePossibility,
    @Relation(
        parentColumn = "ctrl_point_data_verification_type_ref",
        entityColumn = "ctrl_point_verification_type_id"
    )
    val verificationType: ControlPointVerificationType
)

@Dao
interface ControlPointDataDao : BaseDao<ControlPointData>
{
    @Transaction
    @Query("SELECT * FROM ControlPointsData")
    suspend fun getControlPointWithDataWithChoiceWithVerification(): List<ControlPointDataVgpTest>
}