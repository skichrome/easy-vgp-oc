package com.skichrome.oc.easyvgp.androidmanagers

import android.net.ConnectivityManager

interface NetManager
{
    val connManager: ConnectivityManager
    val isConnected: Boolean

    fun isConnectedToInternet(): Boolean
}