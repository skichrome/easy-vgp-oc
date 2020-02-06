package com.skichrome.oc.easyvgp.util

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Activity.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Fragment.toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

fun Activity.errorLog(msg: String) = Log.e(javaClass.simpleName, msg)
fun Fragment.errorLog(msg: String) = Log.e(javaClass.simpleName, msg)

fun View.snackBar(msg: String)
{
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).run { show() }
}