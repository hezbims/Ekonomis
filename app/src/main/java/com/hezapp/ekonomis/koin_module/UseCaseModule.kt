package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.CreateOrUpdateInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.DeleteInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.GetFullInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.GetAllProductsUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.InsertNewProductUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.AddNewProfileUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.GetListProfileUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.EditMonthlyStockUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.GetLatestPreviousMonthStock
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.GetTransactionSummaryOfAMonthUseCase
import com.hezapp.ekonomis.product_preview.domain.use_case.GetPreviewProductSummariesUseCase
import com.hezapp.ekonomis.transaction_history.domain.use_case.GetPreviewTransactionHistoryUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    factory { AddNewProfileUseCase(repo = get()) }
    factory { GetListProfileUseCase(repo = get()) }

    factory { GetAllProductsUseCase(repo = get()) }
    factory { InsertNewProductUseCase(repo = get()) }

    factory { GetPreviewTransactionHistoryUseCase(repo = get()) }

    factory { CreateOrUpdateInvoiceUseCase(get()) }
    factory { DeleteInvoiceUseCase(get()) }
    factory { GetFullInvoiceUseCase(
        repo = get()
    ) }

    factory { GetPreviewProductSummariesUseCase(
        repo = get()
    ) }

    factory { GetTransactionSummaryOfAMonthUseCase(
        invoiceItemRepo = get(),
        monthlyStockRepo = get(),
    ) }
    factory { GetLatestPreviousMonthStock(
        getTransactionSummaryOfAMonth = get()
    ) }

    factory { GetProductDetailUseCase(
        productRepo = get(),
        getTransactionSummaryOfAMonth = get(),
        monthlyStockRepo = get(),
        transactionProvider = get(),
    ) }

    factory { EditMonthlyStockUseCase(
        repo = get(),
        transactionProvider = get(),
    ) }
}