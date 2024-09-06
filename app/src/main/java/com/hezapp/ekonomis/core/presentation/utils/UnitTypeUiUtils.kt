package com.hezapp.ekonomis.core.presentation.utils

import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType

fun UnitType.getStringId() : Int =
    when(this){
        UnitType.CARTON -> R.string.carton_label
        UnitType.PIECE -> R.string.piece_label
    }