package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteControlPointData
    (
    val id: Long,
    val ctrlPointRef: Long,
    val ctrlPointPossibility: Int,
    val ctrlPointVerificationType: Int,
    val comment: String?
)
{
    constructor() : this(0L, 0L, 0, 0, null)
}