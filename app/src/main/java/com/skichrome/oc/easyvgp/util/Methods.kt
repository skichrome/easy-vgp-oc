package com.skichrome.oc.easyvgp.util

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun Activity.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Fragment.toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

fun Activity.errorLog(msg: String) = Log.e(javaClass.simpleName, msg)
fun Fragment.errorLog(msg: String) = Log.e(javaClass.simpleName, msg)

fun View.snackBar(msg: String)
{
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).run { show() }
}

fun RecyclerView.ViewHolder.setHolderBottomMargin(isLastIndex: Boolean)
{
    val params = itemView.layoutParams as RecyclerView.LayoutParams
    // Set a bottom margin for last item in list, without it the FAB will hide last item actions.
    params.bottomMargin = if (isLastIndex) 250 else 0
    itemView.layoutParams = params
}

// --- Use Task with coroutines --- //

@Suppress("UNCHECKED_CAST")
suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        when
        {
            task.result is QuerySnapshot && (task.result as QuerySnapshot).metadata.isFromCache ->
                continuation.resumeWithException(Exception("without internet"))
            task.result is DocumentSnapshot && (task.result as DocumentSnapshot).metadata.isFromCache ->
                continuation.resumeWithException(Exception("without internet"))
            task.result is Uri && (task.result as Uri).path == null ->
                continuation.resumeWithException(Exception("path is null !!"))
            task.isCanceled ->
                continuation.cancel()
            task.isSuccessful ->
                continuation.resume(task.result as T)
        }
    }
    addOnFailureListener { continuation.resumeWithException(it) }
}