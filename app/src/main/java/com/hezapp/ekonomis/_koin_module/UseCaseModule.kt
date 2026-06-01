package com.hezapp.ekonomis._koin_module

import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.CreateOrUpdateInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.DeleteInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.GetFullInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.ICreateOrUpdateInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.IDeleteInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.IGetFullInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.GetAllProductsUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.InsertNewProductUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.AddNewProfileUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.GetListProfileUseCase
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface.IEditProductNameUseCase
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface.IGetProductByIdUseCase
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.impl.EditProductNameUseCase
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.impl.GetProductByIdUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.EditMonthlyStockUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.GetLatestPreviousMonthStock
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.GetTransactionSummaryOfAMonthUseCase
import com.hezapp.ekonomis.product_preview.application.use_case.iface.IGetPreviewProductSummariesUseCase
import com.hezapp.ekonomis.product_preview.application.use_case.impl.GetPreviewProductSummariesUseCase
import com.hezapp.ekonomis.transaction_history.application.use_case.iface.IGetPreviewTransactionHistoryUseCase
import com.hezapp.ekonomis.transaction_history.application.use_case.impl.GetPreviewTransactionHistoryUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    factory { AddNewProfileUseCase(repo = get(), reportingService = get()) }
    factory { GetListProfileUseCase(repo = get(), reportingService = get()) }

    factory { GetAllProductsUseCase(repo = get(), reportingService = get()) }
    factory { InsertNewProductUseCase(repo = get(), reportingService = get())}

    factory<IGetPreviewTransactionHistoryUseCase> { GetPreviewTransactionHistoryUseCase(
        dao = get(),
        reportingService = get(),
        timeService = get(),
    ) }

    factory<ICreateOrUpdateInvoiceUseCase> { CreateOrUpdateInvoiceUseCase(get(), reportingService = get()) }
    factory<IDeleteInvoiceUseCase> { DeleteInvoiceUseCase(get(), reportingService = get()) }
    factory<IGetFullInvoiceUseCase> { GetFullInvoiceUseCase(
        repo = get(),
        reportingService = get(),
    ) }

    factory<IGetPreviewProductSummariesUseCase> { GetPreviewProductSummariesUseCase(
        dao = get(), reportingService = get(),
    ) }

    factory { GetTransactionSummaryOfAMonthUseCase(
        getProductTransactions = get(),
        getMonthlyStockByPeriod = get(),
        timeService = get(),
    ) }
    factory { GetLatestPreviousMonthStock(
        getTransactionSummaryOfAMonth = get(),
        reportingService = get(),
        timeService = get(),
    ) }

    factory { GetProductDetailUseCase(
        productRepo = get(),
        getTransactionSummaryOfAMonth = get(),
        monthlyStockRepo = get(),
        transactionProvider = get(),
        reportingService = get(),
    ) }

    factory { EditMonthlyStockUseCase(
        repo = get(),
        transactionProvider = get(),
        reportingService = get(),
    ) }

    factory<IEditProductNameUseCase> { EditProductNameUseCase(repo = get(), reportingService = get()) }
    factory<IGetProductByIdUseCase> { GetProductByIdUseCase(dao = get()) }
}