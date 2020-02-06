package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customers(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val siret: String
)