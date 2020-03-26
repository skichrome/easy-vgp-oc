package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteReportData(
    val customer: RemoteCustomer,
    val machineType: RemoteMachineType,
    val machine: RemoteMachine,
    val reportData: LinkedHashMap<String, RemoteControlPointData>
)
{
    constructor() : this(RemoteCustomer(), RemoteMachineType(), RemoteMachine(), LinkedHashMap())
}