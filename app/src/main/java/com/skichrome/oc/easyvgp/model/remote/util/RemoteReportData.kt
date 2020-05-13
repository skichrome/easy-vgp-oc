package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteReportData(
    val user: RemoteUser,
    val customer: RemoteCustomer,
    val machineType: RemoteMachineType,
    val machine: RemoteMachine,
    val reportData: LinkedHashMap<String, RemoteControlPointData>,
    val reportCtrlPoint: LinkedHashMap<String, RemoteControlPoint>,
    val reportDataExtra: RemoteMachineControlPointDataExtra
)
{
    constructor() : this(
        RemoteUser(),
        RemoteCustomer(),
        RemoteMachineType(),
        RemoteMachine(),
        LinkedHashMap(),
        LinkedHashMap(),
        RemoteMachineControlPointDataExtra()
    )
}