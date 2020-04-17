package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.R

enum class ControlType(val id: Int, val type: Int)
{
    PUT_INTO_SERVICE(0, R.string.ctrl_type_one),
    VGP(1, R.string.ctrl_type_two),
    PUT_BACK_INTO_SERVICE(2, R.string.ctrl_type_three)
}