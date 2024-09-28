package com.hezapp.ekonomis.core.domain.invoice_item.entity

enum class UnitType(val id : Int) {
    CARTON(CARTON_ID), PIECE(PIECE_ID)
}

const val CARTON_ID = 0
const val PIECE_ID = 1