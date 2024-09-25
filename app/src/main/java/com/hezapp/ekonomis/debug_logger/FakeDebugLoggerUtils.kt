package com.hezapp.ekonomis.debug_logger

import android.util.Log
import com.hezapp.ekonomis.core.data.invoice.FakeInvoiceRepo
import com.hezapp.ekonomis.core.data.invoice_item.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.data.product.repo.FakeProductRepo
import com.hezapp.ekonomis.core.data.profile.repo.FakeProfileRepo
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

class FakeDebugLoggerUtils {
    companion object {
        fun logAllInvoices(){
           val contents = FakeInvoiceRepo.listData.joinToString("\n\n") {
               it.toLogString()
           }
            Log.e("qqqInvoices", contents)
        }

        private fun InvoiceEntity.toLogString() : String {
            val profile =
                FakeProfileRepo.listPerson.single { profile -> profile.id == profileId }
            return """id : ${id}, 
date : ${date}, 
ppn : ${ppn}, 
transaction type : ${transactionType.toLogString()}
profile : ${profile.name}""".trimIndent()
        }

        fun logAllInvoiceItems(
            listItem: List<InvoiceItemEntity> = FakeInvoiceItemRepo.listItem,
            tag: String = "qqqInvoice Items"
        ){
            val contents = listItem.joinToString("\n\n") {
                it.toLogString()
            }
            Log.e(tag, contents)
        }

        private fun InvoiceItemEntity.toLogString() : String {
            val product = FakeProductRepo.listProduct.single { it.id == productId }
            return """id : $id,
invoice id : $invoiceId
product name : ${product.name},
price : $price,
quantity : $quantity,
unit type : ${unitType.toLogString()}
            """.trimIndent()
        }

        private fun UnitType.toLogString() : String {
            return when(this){
                UnitType.CARTON -> "Carton"
                UnitType.PIECE -> "Piece"
            }
        }

        private fun TransactionType.toLogString() : String {
            return when(this){
                TransactionType.PEMBELIAN -> "Pembelian"
                TransactionType.PENJUALAN -> "Penjualan"
            }
        }
    }
}