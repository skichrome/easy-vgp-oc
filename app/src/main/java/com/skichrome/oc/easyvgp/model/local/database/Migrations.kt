package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2)
{
    override fun migrate(database: SupportSQLiteDatabase)
    {
        database.execSQL("ALTER TABLE MachinesControlPointsDataExtras ADD COLUMN is_tests_with_load INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE MachinesControlPointsDataExtras ADD COLUMN is_tests_with_nominal_load INTEGER DEFAULT NULL")
        database.execSQL("ALTER TABLE MachinesControlPointsDataExtras ADD COLUMN is_tests_triggered_sensors INTEGER DEFAULT NULL")
        database.execSQL("ALTER TABLE MachinesControlPointsDataExtras ADD COLUMN test_load_type TEXT DEFAULT NULL")
        database.execSQL("ALTER TABLE MachinesControlPointsDataExtras ADD COLUMN test_load_mass_in_kilos INTEGER DEFAULT NULL")
        database.execSQL("ALTER TABLE MachinesControlPointsDataExtras ADD COLUMN control_general_result INTEGER NOT NULL DEFAULT 0")
    }
}