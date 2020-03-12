package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Company::class,
        User::class,

        Customer::class,
        Machine::class,

        MachineType::class,
        ControlPoint::class,
        ControlPointData::class,

        MachineTypeControlPointCrossRef::class,
        MachineControlPointData::class
    ],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun companiesDao(): CompanyDao
    abstract fun usersDao(): UserDao

    abstract fun customersDao(): CustomerDao
    abstract fun machinesDao(): MachineDao

    abstract fun machinesTypeDao(): MachineTypeDao
    abstract fun controlPointDao(): ControlPointDao
    abstract fun controlPointDataDao(): ControlPointDataDao

    abstract fun machineTypeControlPointCrossRefDao(): MachineTypeControlPointCrossRefDao
    abstract fun machineControlPointDataDao(): MachineControlPointDataDao
}