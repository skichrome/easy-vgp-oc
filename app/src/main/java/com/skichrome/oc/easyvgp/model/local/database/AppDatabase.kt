package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Company::class, User::class, Customer::class, Machine::class, MachineType::class],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun companiesDao(): CompanyDao
    abstract fun usersDao(): UserDao

    abstract fun customersDao(): CustomersDao
    abstract fun machinesDao(): MachinesDao
    abstract fun machinesTypeDao(): MachineTypeDao
}