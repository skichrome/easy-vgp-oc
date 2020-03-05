package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.*

@Entity(tableName = "machine_type_control_point_cross_ref", primaryKeys = ["machine_type_id", "control_point_id"])
data class MachineTypeControlPointCrossRef(
    @ColumnInfo(name = "machine_type_id") val machineId: Long,
    @ColumnInfo(name = "control_point_id", index = true) val ctrlPointId: Long
)

data class MachineTypeWithControlPoints(
    @Embedded val machineType: MachineType,
    @Relation(
        parentColumn = "machine_type_id",
        entityColumn = "control_point_id",
        associateBy = Junction(MachineTypeControlPointCrossRef::class)
    )
    val controlPoints: List<ControlPoint>
)