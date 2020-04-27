package com.skichrome.oc.easyvgp.model.local.database

import android.net.Uri
import androidx.room.TypeConverter
import com.skichrome.oc.easyvgp.model.local.ControlType

class Converters
{
    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun stringToUri(str: String?): Uri? = str?.let { Uri.parse(str) }

    @TypeConverter
    fun controlTypeToInt(ctrlType: ControlType): Int = ctrlType.id

    @TypeConverter
    fun intToControlType(int: Int): ControlType = ControlType.values().let {
        if (int > it.size)
            throw IllegalArgumentException("Passed integer not in control type array range !")
        it[int]
    }

    @TypeConverter
    fun controlResultToInt(ctrlResult: ControlResult): Int = ctrlResult.id

    @TypeConverter
    fun intToControlResult(int: Int): ControlResult = ControlResult.values().let {
        if (int > it.size)
            throw IllegalArgumentException("Passed integer not in control result array range !")
        it[int]
    }
}