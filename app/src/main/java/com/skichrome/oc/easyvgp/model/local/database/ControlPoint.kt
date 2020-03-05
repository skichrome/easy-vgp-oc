package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "control_points")
data class ControlPoint(
    @ColumnInfo(name = "control_point_id") @PrimaryKey val id: Long,
    @ColumnInfo(name = "control_point_code", index = true) val code: String,
    @ColumnInfo(name = "control_point_name") val name: String
)

@Dao
interface ControlPointDao : BaseDao<ControlPoint>
{
    @Query("SELECT * FROM control_points")
    fun observeControlPoints(): LiveData<List<ControlPoint>>
}