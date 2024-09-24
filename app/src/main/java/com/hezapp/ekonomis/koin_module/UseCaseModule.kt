package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.AddNewProfileUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.GetListProfileUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    factory { AddNewProfileUseCase(repo = get()) }
    factory { GetListProfileUseCase(repo = get()) }
}