package com.skichrome.oc.easyvgp.util

import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.os.Environment.*
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// =================================
//              Fields
// =================================

const val PDF_FOLDER_NAME = "pdf_reports"
const val AUTHORITY = "com.skichrome.oc.easyvgp.fileprovider"

// =================================
//              Methods
// =================================

// --- External storage State methods --- //

fun Environment.canWriteExternalStorage(): Boolean
{
    val state = getExternalStorageState()
    return state == MEDIA_MOUNTED
}

fun Environment.canReadExternalStorage(): Boolean
{
    val state = getExternalStorageState()
    return state == MEDIA_MOUNTED || state == MEDIA_MOUNTED_READ_ONLY
}

// --- File IO operations --- //

fun File.createOrGetFile(folderName: String, fileName: String): File
{
    val fileFolder = File(this, folderName)
    return File(fileFolder, fileName)
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