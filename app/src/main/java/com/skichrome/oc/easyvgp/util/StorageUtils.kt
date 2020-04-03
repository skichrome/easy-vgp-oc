package com.skichrome.oc.easyvgp.util

import android.graphics.pdf.PdfDocument
import android.os.Environment.*
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// =================================
//              Fields
// =================================

const val PICTURES_FOLDER_NAME = "user_medias"
const val REQUEST_IMAGE_CAPTURE = 333
const val PDF_FOLDER_NAME = "pdf_reports"
const val AUTHORITY = "com.skichrome.oc.easyvgp.fileprovider"

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

fun File.writePdfToFile(pdf: PdfDocument)
{
    try
    {
        parentFile?.mkdirs()
        FileOutputStream(this)
            .use { pdf.writeTo(it) }
    }
    catch (e: IOException)
    {
        Log.e("StorageUtils", "Cannot open file from storage", e)
    }
}