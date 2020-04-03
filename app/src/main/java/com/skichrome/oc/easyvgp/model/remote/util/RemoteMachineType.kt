package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteMachineType(val id: Long = 0L, val legalName: String = "", val name: String = "")
{
    constructor() : this(0L, "", "")
}
