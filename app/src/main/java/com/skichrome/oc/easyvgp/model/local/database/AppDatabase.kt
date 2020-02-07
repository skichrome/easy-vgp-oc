package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Customers::class],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun customersDao(): CustomersDao
}