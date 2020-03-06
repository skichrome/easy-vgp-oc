package com.skichrome.oc.easyvgp.model.remote

class RemoteMachineTypeWithControlPoints(
    val remoteMachineType: RemoteMachineType = RemoteMachineType(),
    val controlPoints: List<RemoteControlPoint> = emptyList()
)
{
    constructor() : this(RemoteMachineType(), emptyList())
}