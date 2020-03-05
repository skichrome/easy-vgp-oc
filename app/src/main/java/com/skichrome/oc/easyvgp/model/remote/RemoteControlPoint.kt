package com.skichrome.oc.easyvgp.model.remote

class RemoteControlPoint(val id: Long = 0L, val code: String = "", val name: String = "", val remoteId: String = "")
{
    constructor() : this(0L, "", "", "")
}