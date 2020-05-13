package com.skichrome.oc.easyvgp.model.local.util

import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
import com.skichrome.oc.easyvgp.model.local.database.ControlPoint

data class ControlPointDataVgp(
    val ctrlPointDataId: Long,
    val controlPoint: ControlPoint,
    var comment: String? = null,
    var choicePossibility: ChoicePossibility,
    var verificationType: VerificationType
)