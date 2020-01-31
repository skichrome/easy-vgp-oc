package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customers(
    @PrimaryKey val id: Long,
    val name: String,
    val siret: String
)