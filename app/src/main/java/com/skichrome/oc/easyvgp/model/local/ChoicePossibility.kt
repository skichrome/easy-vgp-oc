package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.R

enum class ChoicePossibility(val id: Int, val choice: Int)
{
    UNKNOWN(0, 0),
    GOOD(1, R.string.choice_possibilities_good),
    MEDIUM(2, R.string.choice_possibilities_medium),
    BAD(3, R.string.choice_possibilities_bad)
}