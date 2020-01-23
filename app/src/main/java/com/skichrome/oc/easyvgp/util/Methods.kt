package com.skichrome.oc.easyvgp.util

import android.widget.Toast
import com.skichrome.oc.easyvgp.view.base.BaseFragment

fun BaseFragment.toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()