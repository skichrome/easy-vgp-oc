package com.skichrome.oc.easyvgp.model.remote.util

class RemoteMachineTypeWithControlPoints(
    val machineType: RemoteMachineType = RemoteMachineType(),
    val controlPoints: List<RemoteControlPoint> = emptyList()
)
{
    constructor() : this(RemoteMachineType(), emptyList())
}