package com.skichrome.oc.easyvgp.model.source

import android.net.ConnectivityManager
import com.skichrome.oc.easyvgp.androidmanagers.NetManager

class FakeNetManager(var isFakeConnected: Boolean) : NetManager
{
    override val connManager: ConnectivityManager
        get() = throw NotImplementedError("Not implemented in tests")

    override val isConnected: Boolean
        get() = throw NotImplementedError("Not implemented in tests")

    override fun isConnectedToInternet(): Boolean = isFakeConnected

    fun setIsFakeConnected(state: Boolean)
    {
        isFakeConnected = state
    }
}