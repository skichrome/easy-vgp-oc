package com.skichrome.oc.easyvgp.model.remote

import com.skichrome.oc.easyvgp.model.local.database.ControlPoint

class RemoteMachineTypeWithControlPoints(
    val remoteMachineType: RemoteMachineType = RemoteMachineType(),
    val controlPoints: List<ControlPoint> = emptyList()
)
{
    constructor() : this(RemoteMachineType(), emptyList())
}