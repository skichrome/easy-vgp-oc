package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

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
        MachineControlPointDataExtra::class,
        MachineControlPointData::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
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
    abstract fun machineControlPointDataExtraDao(): MachineControlPointDataExtraDao
    abstract fun machineControlPointDataDao(): MachineControlPointDataDao
}