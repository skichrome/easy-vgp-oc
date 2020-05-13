package com.skichrome.oc.easyvgp.model.local.util

import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.MachineType

data class MachineTypeCtrlPtMultiChoiceItems(
    val machineType: MachineType,
    val ctrlPoint: ControlPoint,
    var isChecked: Boolean
)