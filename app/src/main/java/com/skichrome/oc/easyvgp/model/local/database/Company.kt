package com.skichrome.oc.easyvgp.model.local.database

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Companies")
data class Company(
    @ColumnInfo(name = "company_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "company_name") val name: String,
    @ColumnInfo(name = "company_siret") val siret: String,
    @ColumnInfo(name = "company_local_logo") val localCompanyLogo: Uri?,
    @ColumnInfo(name = "company_remote_logo") var remoteCompanyLogo: Uri? = null
)

@Dao
interface CompanyDao : BaseDao<Company>