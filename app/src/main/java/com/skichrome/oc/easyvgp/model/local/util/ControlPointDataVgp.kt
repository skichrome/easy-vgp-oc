package com.skichrome.oc.easyvgp.model.local.util

import com.skichrome.oc.easyvgp.model.local.database.ControlPoint
import com.skichrome.oc.easyvgp.model.local.database.ControlPointChoicePossibility
import com.skichrome.oc.easyvgp.model.local.database.ControlPointData
import com.skichrome.oc.easyvgp.model.local.database.ControlPointVerificationType

data class ControlPointDataVgp(
    val controlPoint: ControlPoint,
    val controlPointData: ControlPointData,
    val choicePossibility: ControlPointChoicePossibility,
    val verificationType: ControlPointVerificationType
)