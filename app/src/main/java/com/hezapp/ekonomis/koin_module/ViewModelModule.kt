package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product.SearchAndChooseProductViewModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile.SearchAndChooseProfileViewModel
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
}