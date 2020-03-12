package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.R

enum class VerificationType(verification: Int)
{
    VISUAL(R.string.verification_type_visual),
    VISUAL_FUNCTIONAL(R.string.verification_type_both_visual_functional),
    FUNCTIONAL(R.string.verification_type_functional)
}