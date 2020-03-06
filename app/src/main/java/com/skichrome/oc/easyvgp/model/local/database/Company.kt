package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Companies")
data class Company(
    @ColumnInfo(name = "company_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "company_name") val name: String,
    @ColumnInfo(name = "company_siret") val siret: String
)

@Dao
interface CompanyDao : BaseDao<Company>