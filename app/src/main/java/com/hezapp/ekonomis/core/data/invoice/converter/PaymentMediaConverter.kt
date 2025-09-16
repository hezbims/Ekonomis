package com.hezapp.ekonomis.core.data.invoice.converter

import androidx.room.TypeConverter
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia

class PaymentMediaConverter {
    @TypeConverter
    fun intToPaymentMedia(value : Int?) : PaymentMedia? =
        value?.let {
            PaymentMedia.from(it)
        }
    @TypeConverter
    fun paymentMediaToInt(value : PaymentMedia?) : Int? =
        value?.id
}