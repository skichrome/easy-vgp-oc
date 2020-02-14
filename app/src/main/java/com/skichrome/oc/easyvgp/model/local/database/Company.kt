package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "Companies")
data class Company(
    @ColumnInfo(name = "company_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "company_name") val name: String,
    @ColumnInfo(name = "company_siret") val siret: String
)

@Dao
interface CompanyDao : BaseDao<Company>
{
    @Query("SELECT * FROM Companies")
    fun observeCompanies(): LiveData<List<Company>>
}