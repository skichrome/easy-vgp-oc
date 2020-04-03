package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteControlPointData
    (
    val id: Long,
    val ctrlPointRef: Long,
    val ctrlPointPossibility: String,
    val ctrlPointVerificationType: String,
    val comment: String?
)
{
    constructor() : this(0L, 0L, "", "", null)
}