package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.data.profile.repo.ProfileRepo
import com.hezapp.ekonomis.core.domain.profile.repo.IProfileRepo
import org.koin.dsl.module

val RepositoryModule = module {
    single<IProfileRepo> { ProfileRepo(dao = get()) }
}