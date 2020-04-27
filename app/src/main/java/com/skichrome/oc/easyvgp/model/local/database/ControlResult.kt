package com.skichrome.oc.easyvgp.model.local.database

import com.skichrome.oc.easyvgp.R

enum class ControlResult(val id: Int, val result: Int)
{
    RESULT_OK(0, R.string.ctrl_result_ok),
    RESULT_OK_WITH_INTERVENTION_NEEDED(1, R.string.ctrl_result_ok_but_need_repair),
    RESULT_KO(2, R.string.ctrl_result_ko)
}