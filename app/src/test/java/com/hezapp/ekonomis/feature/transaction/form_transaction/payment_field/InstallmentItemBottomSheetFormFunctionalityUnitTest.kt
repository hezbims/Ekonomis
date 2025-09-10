package com.hezapp.ekonomis.feature.transaction.form_transaction.payment_field

import androidx.compose.material3.ExperimentalMaterial3Api
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component.InstallmentItemBottomSheetForm
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.dto.InstallmentItemUiDto
import com.hezapp.ekonomis.robot.transaction_form._interactor.InstallmentItemFormInteractor
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.TestTimeService
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
class InstallmentItemBottomSheetFormFunctionalityUnitTest : BaseEkonomisUiUnitTest() {
    private val formInteractor = InstallmentItemFormInteractor(composeRule, appContext)

    @Test
    fun `the date field should filled with current date and the amount field is empty, when the form is opened without initial data`(){
        val currentDate = TestTimeService.Companion.get().getLocalDate()

        composeRule.setContent {
            InstallmentItemBottomSheetForm(
                visible = true,
                onDismissRequest = { },
                onSaveData = { },
                timeService = TestTimeService.Companion.get(),
            )
        }

        formInteractor.assertDateFieldContent(currentDate)
        formInteractor.assertAmountFieldContent("")
    }

    @Test
    fun `the date and amount field should fill correctly when opened with initial data`(){
        val initialDate = TestTimeService.Companion.get().getLocalDate()
            .withYear(2025)
            .withMonth(8)
            .withDayOfMonth(9)
        val initialAmount = 2_000_000

        composeRule.setContent {
            InstallmentItemBottomSheetForm(
                visible = true,
                onDismissRequest = { },
                onSaveData = { },
                initialData = InstallmentItemUiDto(
                    date = initialDate,
                    amount = initialAmount,
                ),
                timeService = TestTimeService.Companion.get(),
            )
        }

        formInteractor.assertDateFieldContent(initialDate)
        formInteractor.assertAmountFieldContent("Rp2.000.000")
    }

    @Test
    fun `user should see error message when the user submit the form and amount is empty`(){
        // arrange
        composeRule.setContent {
            InstallmentItemBottomSheetForm(
                visible = true,
                onDismissRequest = { },
                onSaveData = { },
                timeService = TestTimeService.Companion.get(),
            )
        }

        // act
        formInteractor.submit()

        // assert
        formInteractor.assertAmountFieldHasError()
    }

    @Test
    fun `user should see error message when the user submit the form and amount is zero`(){
        // arrange
        composeRule.setContent {
            InstallmentItemBottomSheetForm(
                visible = true,
                onDismissRequest = { },
                onSaveData = { },
                timeService = TestTimeService.Companion.get(),
            )
        }

        // act
        formInteractor.inputTextInAmountField("0")
        formInteractor.submit()

        // assert
        formInteractor.assertAmountFieldHasError()
    }

    @Test
    fun `when user start modifying amount field after the error appears, the error should disappear`(){
        // arrange
        composeRule.setContent {
            InstallmentItemBottomSheetForm(
                visible = true,
                onDismissRequest = { },
                onSaveData = { },
                timeService = TestTimeService.Companion.get(),
            )
        }

        // act
        formInteractor.submit()
        formInteractor.inputTextInAmountField("1")

        // assert
        formInteractor.assertAmountFieldDoesntHaveError()
    }

    @Test
    fun `save action should happen with correct saved data when user press the submit button and amount field is valid`(){
        // Arrange
        var savedData : InstallmentItemUiDto? = null
        var dismissCalled = false

        composeRule.setContent {
            InstallmentItemBottomSheetForm(
                visible = true,
                onDismissRequest = {
                    dismissCalled = true
                },
                onSaveData = {
                    savedData = it
                },
                timeService = TestTimeService.Companion.get(),
            )
        }

        // Act
        formInteractor.inputTextInAmountField("1500000")
        formInteractor.selectDate(
            LocalDate.now()
            .withYear(2023)
            .withMonth(12)
            .withDayOfMonth(12))
        formInteractor.submit()

        // Assert
        val expectedDate = LocalDate.now()
            .withYear(2023)
            .withMonth(12)
            .withDayOfMonth(12)
        val expectedAmount = 1_500_000

        composeRule.runOnIdle {
            MatcherAssert.assertThat(
                savedData?.date?.isEqual(expectedDate),
                CoreMatchers.equalTo(true)
            )
            MatcherAssert.assertThat(savedData?.amount, CoreMatchers.equalTo(expectedAmount))
            MatcherAssert.assertThat(dismissCalled, CoreMatchers.equalTo(true))
        }
    }


}