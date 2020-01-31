package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T>
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(vararg obj: T): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(vararg obj: T): List<Long>

    @Update
    fun update(obj: T): Int

    @Update
    fun update(vararg obj: T): Int

    @Delete
    fun delete(obj: T): Int

    @Delete
    fun delete(vararg obj: T): Int
}