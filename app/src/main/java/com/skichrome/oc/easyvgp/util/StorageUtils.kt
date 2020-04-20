package com.skichrome.oc.easyvgp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// =================================
//              Methods
// =================================

// --- External storage State methods --- //

fun canWriteExternalStorage(): Boolean
{
    val state = getExternalStorageState()
    return state == MEDIA_MOUNTED
}

fun canReadExternalStorage(): Boolean
{
    val state = getExternalStorageState()
    return state == MEDIA_MOUNTED || state == MEDIA_MOUNTED_READ_ONLY
}

// --- File IO operations --- //

@Throws(IOException::class)
fun File.createOrGetJpegFile(folderName: String, fileName: String): File
{
    val fileFolder = File(this, folderName).apply {
        if (!exists())
            mkdirs()
    }
    val timeStamp = SimpleDateFormat("HH:mm:ss_dd-MM-yyyy", Locale.getDefault()).format(Date())
    return File(fileFolder, "[$timeStamp]-$fileName.jpg")
}

@Throws(IOException::class)
fun File.createOrGetFile(folderName: String, fileName: String): File
{
    val fileFolder = File(this, folderName).apply {
        if (!exists())
            mkdirs()
    }
    return File(fileFolder, fileName)
}

@Throws(Exception::class)
fun File.transformBitmapFile(): Bitmap
{
    if (extension != "jpg")
        throw IllegalArgumentException("File to transform must be a JPG image, not $extension !")

    val bmOpt = BitmapFactory.Options()
    val bitmap = BitmapFactory.decodeFile(absolutePath, bmOpt)
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)

    if (bitmap.width < bitmap.height)
    {
        val matrix = Matrix()
        matrix.postRotate(90f)
        val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        try
        {
            FileOutputStream(absolutePath).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            return rotatedBitmap
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
    }
    return scaledBitmap
}