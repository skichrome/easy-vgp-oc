package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.R

enum class VerificationType(val id: Int, val verification: Int)
{
    VISUAL(0, R.string.verification_type_visual),
    VISUAL_FUNCTIONAL(1, R.string.verification_type_both_visual_functional),
    FUNCTIONAL(2, R.string.verification_type_functional)
}