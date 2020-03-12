package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "ControlPointChoicePossibilities")
class ControlPointChoicePossibility(
    @ColumnInfo(name = "ctrl_point_choice_possibility_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "ctrl_point_choice_possibility_name") val name: String
)

@Dao
interface ControlPointChoicePossibilityDao
{
    @Query("SELECT * FROM ControlPointChoicePossibilities")
    fun observePossibilities(): LiveData<List<ControlPointChoicePossibility>>
}