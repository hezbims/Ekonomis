package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionViewModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product.SearchAndChooseProductViewModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile.SearchAndChooseProfileViewModel
import com.hezapp.ekonomis.product_detail.presentation.ProductDetailViewModel
import com.hezapp.ekonomis.product_preview.presentation.ProductPreviewViewModel
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { param -> SearchAndChooseProfileViewModel(
        transactionType = param.get(),
        addNewProfile = get(),
        getListProfile = get(),
    ) }

    viewModel { SearchAndChooseProductViewModel(
        getAllProducts = get(),
        insertNewProduct = get(),
    ) }

    viewModel { TransactionHistoryViewModel(
        getPreviewTransactionHistory = get()
    ) }

    viewModel { (invoiceId: Int?) -> AddOrUpdateTransactionViewModel(
        invoiceId = invoiceId,
        createOrUpdateInvoiceUseCase = get(),
        deleteInvoice = get(),
        getFullInvoice = get()
    ) }

    viewModel { ProductPreviewViewModel(getPreviewProductSummaries = get()) }

    viewModel { params -> ProductDetailViewModel(
        productId = params.get(),
        getProductDetail = get()
    ) }
}