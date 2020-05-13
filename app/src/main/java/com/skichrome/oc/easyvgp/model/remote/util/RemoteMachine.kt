package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteMachine(
    val machineId: Long = 0L,
    val name: String = "",
    val serial: String = "",
    val brand: String = "",
    val model: String = "",
    val manufacturingYear: Int = 0,
    val photoReference: String? = null,
    val customer: Long = 0L,
    val type: Long = 0L
)
{
    constructor() : this(0L, "", "", "", "", 0, null, 0L, 0L)
}