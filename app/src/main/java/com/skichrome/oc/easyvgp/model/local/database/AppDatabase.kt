package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Customers::class, Machines::class, MachineType::class],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun customersDao(): CustomersDao
    abstract fun machinesDao(): MachinesDao
    abstract fun machinesTypeDao(): MachineTypeDao
}