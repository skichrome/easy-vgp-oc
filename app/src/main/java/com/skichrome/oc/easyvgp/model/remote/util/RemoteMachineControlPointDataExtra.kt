package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteMachineControlPointDataExtra(
    val id: Long = 0L,
    val reportDate: Long = 0L,
    val reportEndDate: Long = 0L,
    val isMachineClean: Boolean = false,
    val isMachineCE: Boolean = false,
    val machineNotice: Boolean = false,
    val isLiftingEquip: Boolean = false,
    val controlType: String = "",
    val machineHours: Int = -1,
    val interventionPlace: String = ""
)
{
    constructor() : this(0L, 0L, 0L, false, false, false, false, "", -1, "")
}