package com.skichrome.oc.easyvgp.model.local.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Customers::class],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun customersDao(): CustomersDao

    companion object
    {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(app: Application) = INSTANCE ?: synchronized(this) {
            INSTANCE
                ?: buildDatabase(app).also { INSTANCE = it }
        }

        private fun buildDatabase(app: Application): AppDatabase =
            Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "easy-vgp-database.db").build()
    }
}