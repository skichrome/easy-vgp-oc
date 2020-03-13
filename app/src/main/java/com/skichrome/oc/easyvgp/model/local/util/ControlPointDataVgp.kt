package com.skichrome.oc.easyvgp.model.local.util

import com.skichrome.oc.easyvgp.model.local.database.ControlPoint

data class ControlPointDataVgp(
    val ctrlPointDataId: Long,
    val controlPoint: ControlPoint,
    var comment: String? = null,
    var choicePossibilityId: Int,
    var verificationTypeId: Int
)