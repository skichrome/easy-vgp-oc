package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T>
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(vararg obj: T): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(vararg obj: T): List<Long>

    @Update
    suspend fun update(obj: T): Int

    @Update
    suspend fun update(vararg obj: T): Int

    @Delete
    suspend fun delete(obj: T): Int

    @Delete
    suspend fun delete(vararg obj: T): Int
}