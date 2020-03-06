package com.skichrome.oc.easyvgp.util

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun Activity.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Fragment.toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

fun Activity.errorLog(msg: String) = Log.e(javaClass.simpleName, msg)
fun Fragment.errorLog(msg: String) = Log.e(javaClass.simpleName, msg)

fun View.snackBar(msg: String)
{
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).run { show() }
}

@Suppress("UNCHECKED_CAST")
suspend fun <T> Task<T>.awaitQuery(): T = suspendCoroutine { continuation ->
    addOnCompleteListener { task ->
        when
        {
            (task.result as QuerySnapshot).metadata.isFromCache -> continuation.resumeWithException(Exception("without internet"))
            task.isSuccessful -> continuation.resume(value = task.result as T)
            else -> continuation.resumeWithException(task.exception!!)
        }
    }
    addOnFailureListener { continuation.resumeWithException(it) }
}

@Suppress("UNCHECKED_CAST")
suspend fun <T> Task<T>.awaitDocument(): T = suspendCoroutine { continuation ->
    addOnCompleteListener { task ->
        when
        {
            (task.result as DocumentSnapshot).metadata.isFromCache -> continuation.resumeWithException(Exception("without internet"))
            task.isSuccessful -> continuation.resume(value = task.result as T)
            else -> continuation.resumeWithException(task.exception!!)
        }
    }
    addOnFailureListener { continuation.resumeWithException(it) }
}

@Suppress("UNCHECKED_CAST")
suspend fun <T> Task<T>.awaitUpload(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        when
        {
            task.isCanceled -> continuation.cancel()
            task.isSuccessful -> continuation.resume(task.result as T)
        }
    }
    addOnFailureListener { continuation.resumeWithException(it) }
}