package com.hezapp.ekonomis.product_preview.presentation.utils

import android.content.Context
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiah

class ProductPreviewUiUtils {
    companion object {
        fun getCostOfGoodsText(
            costOfGoodsSold: Int?,
            unitType: UnitType?,
            context: Context,
        ) : String {
            return context.getString(
                R.string.cost_of_goods_label,
                if (costOfGoodsSold == null || unitType == null)
                    "-"
                else
                    "${costOfGoodsSold.toRupiah()}/${context.getString(unitType.getStringId())}"
            )
        }
    }
}