package com.skichrome.oc.easyvgp.androidmanagers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class DefaultNetManager(context: Context) : NetManager
{
    override val connManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val isConnected: Boolean
        get()
        {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            {
                val activeNetwork = connManager.activeNetwork ?: return false
                val networkCapabilities = connManager.getNetworkCapabilities(activeNetwork) ?: return false

                when
                {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                    else -> false
                }
            } else
            {
                connManager.run {
                    activeNetworkInfo?.run {
                        isConnected
                    }
                } ?: false
            }
        }

    override fun isConnectedToInternet(): Boolean = isConnected
}