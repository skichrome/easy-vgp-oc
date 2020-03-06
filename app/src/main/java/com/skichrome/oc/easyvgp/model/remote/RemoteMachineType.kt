package com.skichrome.oc.easyvgp.model.remote

data class RemoteMachineType(val id: Long = 0L, val remoteId: String = "", val legalName: String = "", val name: String = "")
{
    constructor() : this(0L, "", "", "")
}
