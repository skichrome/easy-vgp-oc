package com.skichrome.oc.easyvgp.model.local.database

import android.net.Uri
import androidx.room.TypeConverter

class Converters
{
    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun stringToUri(str: String?): Uri? = str?.let { Uri.parse(str) }
}