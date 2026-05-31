package com.hezapp.ekonomis.test_case.di

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.MainApplication
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.product_detail.presentation.EditMonthlyStockDialogViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProvider
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class VerifyKoinGraphTest : KoinTest {
    @Test
    fun `Verify koin graph`(){
        MockProvider.register { clazz ->
            Mockito.mock(clazz.java)
        }
        val koinApp = koinApplication {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(MainApplication.koinModules)
        }
        @Suppress("DEPRECATION")
        koinApp.checkModules {
            withInstance(123)
            withInstance(EditMonthlyStockDialogViewModel.Params(
                quantityPerUnitType = QuantityPerUnitType(cartonQuantity = 1, pieceQuantity = 1),
                period = 1L,
                productId = 1,
                monthlyStockId = 1,
            ))
            withInstance(TransactionType.PEMBELIAN)
        }
    }
}