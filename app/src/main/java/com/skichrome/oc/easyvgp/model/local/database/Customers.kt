package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customers(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val firstName: String,
    val lastName: String,
    val siret: Long,
    val email: String?,
    val phone: Int?,
    val mobilePhone: Int?,
    val address: String,
    val postCode: Int,
    val city: String,
    val notes: String?
)