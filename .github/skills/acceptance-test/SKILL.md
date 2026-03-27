# Automated Acceptance Testing — Skills Guide

> This guide explains how to write an automated acceptance test in this project.
> It covers the architecture, the role of each layer, and a complete step-by-step walkthrough.

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Layer Responsibilities](#2-layer-responsibilities)
3. [Test Lifecycle (Rules & @Before)](#3-test-lifecycle-rules--before)
4. [Step-by-Step: Creating a New Acceptance Test](#4-step-by-step-creating-a-new-acceptance-test)
5. [Principle When Creating Acceptance Tests](#5-principle-when-creating-acceptance-tests)
6. [Full Example](#6-full-example)
7. [Known Issues & Conventions](#7-known-issues--conventions)

---

## 1. Architecture Overview

```
AcceptanceTest (extends BaseGherkinInstrumentedTest<T>)
    └── *TestDefinition (extends BaseTestDefinition)
        └── ITestUtils (robots + steps + seeders + db assertions)
                ├── Robot           ← UI interaction (Page Object Model)
                │     └── _interactor/  ← reusable component-level actions
                ├── Steps           ← reusable multi-screen business flows
                ├── Seeder          ← programmatic test data setup
                └── DbAssertion     ← never use this in acceptance test
```

The test reads in plain Gherkin English:

```kotlin
given.userHasSomeExistingData()
`when`.userPerformsSomeAction()
then.theResultShouldBeCorrect()
and.nothingElseShouldChange()
```

All four (`given`, `when`, `then`, `and`) are aliases pointing to the same
`testDefinition` object. They exist purely for readability.

---

## 2. Layer Responsibilities

### `BaseGherkinInstrumentedTest<T>`
- **Location:** `androidTest/.../test_application/BaseGherkinInstrumentedTest.kt`
- **Role:** The test superclass. Declares JUnit `@Rule`s (time config, compose rule,
  log-on-failure, data cleaner). Exposes `given`/`when`/`then`/`and` as aliases
  for `testDefinition`.
- **When to modify:** Add a new project-wide rule here (e.g., a rule that resets a
  feature flag service).

### `BaseTestDefinition`
- **Location:** `androidTest/.../test_application/BaseTestDefinition.kt`
- **Role:** Implements `ITestUtils` (via delegation to `TestUtils`). All step methods
  that make up the Gherkin vocabulary live in subclasses of this class.
- **When to subclass:** Every acceptance test feature gets its own subclass (e.g.,
  `AddNewTransactionTestDefinition`).

### `Robot` (Page Object Model)
- **Location:** `testFixtures/.../robot/`
- **Role:** Encapsulates all UI interactions for a **single screen** (click, type,
  scroll, assert visibility). Tests never call `composeRule` or `onNode` directly.
- **Sub-folder `_interactor/`:** Reusable wrappers for individual UI components
  (e.g., `TextFieldInteractor`, `DropdownInteractor`). Used *inside* robots,
  not directly in tests.
- **When to create a new robot:** When a new screen is added to the app.
- **When to add to an existing robot:** When a new interaction on an existing screen
  is needed.

### `Steps`
- **Location:** `testFixtures/.../steps/`
- **Role:** Encapsulates a **multi-step business process** that either:
  - spans multiple screens, or
  - is too long/complex to repeat inside a `TestDefinition`.
- **Example:** `FillTransactionFormSteps` handles the entire transaction form flow
  (navigate → fill fields → add products → set payment → submit).
- **When to create a new Steps class:** When a business process recurs across
  multiple test definitions and involves more than one robot.

### `Seeder`
- **Location:** `testFixtures/.../test_utils/seeder/`
- **Role:** Insert test data **directly into the database** (bypassing the UI) so
  that preconditions can be set up fast and reliably. Each seeder returns the
  inserted entity/snapshot so the test can reference it later.
- **When to create a new seeder:** When a new entity type needs to be pre-populated
  for tests.
- **Key rule:** In tests that use seeders, **do not launch `MainActivity` until all
  seeders have finished**. Premature activity launch can cause the ViewModel to
  observe stale data before seeding is complete.

### `DbAssertion`
- **Location:** `testFixtures/.../test_utils/db_assertion/`
- **Role:** Assert on the state of the database **after a UI action**. Complements
  UI assertions (which only verify what the user sees) by ensuring the underlying
  data is also correct.
- **When to use:** never use this in acceptance tests.

---

## 3. Test Lifecycle (Rules & @Before)

Rules run in the order declared by the `order` parameter.

| Order | Rule                       | What it does                                                           |
|-------|----------------------------|------------------------------------------------------------------------|
| 1     | `GlobalTimeConfigRule`     | Sets locale to `id-ID` and timezone to `GMT+8`                         |
| 2     | `createEmptyComposeRule`   | Sets up the Compose testing infrastructure                             |
| 3     | `ComposeTreeLoggerRule`    | Prints the semantics tree to logcat when a test **fails**              |
| 4     | `TestEnvironmentResetRule` | Clears all DB tables and resets `TestTimeService` **before** each test |

`@Before` in `BaseGherkinInstrumentedTest`: optionally launches `MainActivity` if
`immediatelyLaunchMainActivity = true`.

> **Tip:** If your test uses a seeder, set `immediatelyLaunchMainActivity = false`
> (the default). Launch the activity yourself at the end of `@Before` in your
> `TestDefinition`, after all seeding is done.

---

## 4. Step-by-Step: Creating a New Acceptance Test

Assume you want to test: *"Filter transactions by period"*.

### Step 1 — Write the specification with DSL (Gherkin syntax)

```kotlin
// FilterTransactionTest.kt
class FilterTransactionTest : BaseGherkinInstrumentedTest<FilterTransactionTestDefinition>(::FilterTransactionTestDefinition) {

    @Test(timeout = 300_000)
    fun filterByPeriodShouldShowOnlyMatchingTransactions() {
        given.userHasTwoTransactionsFromDifferentPeriodsAndProfiles()
        `when`.userAppliesFilterForTargetPeriod()
        then.onlyTransactionsInTargetPeriodShouldBeVisible()
        and.removingTheFilterShouldShowAllTransactionsAgain()   // ← guards against filter mutating state
    }
}
```

### Step 2 — Create the TestDefinition class

Create a new file in `androidTest/.../acceptance_test/filter_transaction/`:

```kotlin
// FilterTransactionTestDefinition.kt
class FilterTransactionTestDefinition(
    composeTestRule: ComposeTestRule,
    context: Context,
) : BaseTestDefinition(composeTestRule, context) {

    private val targetPeriod = YearMonth.of(2024, 3)
    private val otherPeriod  = YearMonth.of(2024, 6)

    // Two *different* suppliers — one per period — so each card can be identified by profile name.
    private val supplierInTargetPeriod = "Supplier Target"
    private val supplierInOtherPeriod  = "Supplier Lain"

    private lateinit var targetTransaction: InvoiceSnapshot
    private lateinit var otherTransaction:  InvoiceSnapshot

    /**
     * Seeds two distinct transactions for different periods AND different profiles.
     * - Target period: with PPN, fully paid.
     * - Other period:  no PPN, with installment.           ← longest happy path: vary payment type too
     * Using two suppliers lets [onlyTransactionsInTargetPeriodShouldBeVisible] assert
     * absence by profile name rather than by a fabricated string.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun userHasTwoTransactionsFromDifferentPeriodsAndProfiles() = runBlocking {
        val supplier1 = profileSeeder.run(supplierInTargetPeriod, ProfileType.SUPPLIER)
        val supplier2 = profileSeeder.run(supplierInOtherPeriod, ProfileType.SUPPLIER)
        val products  = productSeeder.run(listOf(
            ProductEntity(name = "Tepung Terigu"),
            ProductEntity(name = "Gula Pasir"),
        ))

        targetTransaction = invoiceSeeder.run(
            profile = supplier1,
            date    = LocalDate.of(targetPeriod.year, targetPeriod.monthValue, 5),
            invoiceItems = listOf(
                InvoiceItemSeed(1, UnitType.PIECE, products[0], 50_000)
            ),
            ppn = 11,
        )

        otherTransaction = invoiceSeeder.run(
            profile = supplier2,
            date    = LocalDate.of(otherPeriod.year, otherPeriod.monthValue, 1),
            invoiceItems = listOf(
                InvoiceItemSeed(2, UnitType.PIECE, products[1], 100_000)
            ),
            ppn = null,
            installmentSeed = InstallmentSeed(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemSeed(
                        amount      = 60_000,
                        paymentDate = LocalDate.of(otherPeriod.year, otherPeriod.monthValue, 10),
                    )
                ),
            ),
        )

        ActivityScenario.launch(MainActivity::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun userAppliesFilterForTargetPeriod() {
        transactionHistoryRobot.actionOpenAndApplyFilter(targetPeriod = targetPeriod)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onlyTransactionsInTargetPeriodShouldBeVisible() {
        // The target-period card is present
        transactionHistoryRobot.assertTransactionCardExistWith(
            date = LocalDate.of(targetPeriod.year, targetPeriod.monthValue, 5),
        )
        // The other-period card is completely gone — identified by its real profile name
        transactionHistoryRobot.assertTransactionCardNotExist(supplierInOtherPeriod)
    }
}
```

## 5. Principle when Creating Acceptance Tests

### Longest Happy Path

Is it good that acceptance test to exercise the **longest / most complete happy path** for
the feature under test. This means:

- **Include every optional field or flow that a real user is likely to use.**
  For example, a "add new transaction" test must include an installment payment
  even though payment type is optional — because installment is a first-class
  user journey, not an edge case.
- **Seed the richest realistic precondition.** If the screen shows a list, seed
  more than one item.

### Test as Good Documentation
Write the test in a way that it can serve as documentation for how the feature works.
Avoid implementation details in the DSL that are not relevant to understanding the feature.
Focus on the user perspective and the business rules.

### Test For Unintended Side Effects
In addition to asserting the expected outcome, it is also good to assert that related data is
not unintentionally modified. For example, after adding a new transaction, assert 
that the existing transactions are still present and unchanged. This is great for update
and delete actions, and also good for create actions.


## 6. Full Example

Below is the complete structure for the *"Add New Transaction"* acceptance test.
Notice how it applies the **Longest Happy Path Principle**:
- Two *different* transactions are seeded as preconditions (supplier purchase + customer
  sale with an existing installment), so the test also guards against side-effects.
- The new transaction fills in every optional field: a **brand-new product**,
  **PPN**, and an **installment payment with two items**.
- Assertions cover every form field and every payment detail — not just the basics.

```
acceptance_test/
└── add_new_transaction/
    ├── AddNewTransactionTestDefinition.kt ← Step 3: Gherkin step implementations
    └── AddNewTransactionTest.kt           ← Step 1: The @Test methods
```

**`AddNewTransactionTest.kt`**
```kotlin
class AddNewTransactionTest : BaseGherkinInstrumentedTest<AddNewTransactionTestDefinition>(
    createTestDefinition = ::AddNewTransactionTestDefinition
) {
    @Test(timeout = 300_000)
    fun addNewTransactionTest() {
        given.userHasTwoDistinctTransaction()
        `when`.userAddNewUniqueTransaction()
        then.theNewTransactionShouldAdded()
        and.previousTwoTransactionsShouldNotAffected()
    }
}
```

**`AddNewTransactionTestDefinition.kt`**
```kotlin
class AddNewTransactionTestDefinition(composeTestRule: ComposeTestRule, context: Context)
    : BaseTestDefinition(composeTestRule, context) {

    private val supplierName      = "Supplier Lama"
    private val customerName      = "Pelanggan Lama"
    private val existingProductA  = "Tepung Terigu"
    private val existingProductB  = "Gula Pasir"
    private val newProductName    = "Minyak Goreng"

    private val transaction1Date      = LocalDate.of(2020, 2, 5)
    private val transaction2Date      = LocalDate.of(2020, 2, 10)
    private val newTransactionDate    = LocalDate.of(2020, 2, 20)

    private val transaction1TotalPrice    = 50_000
    private val transaction2TotalPrice    = 75_000
    private val newTransactionTotalPrice  = 100_000

    lateinit var oldSupplierTransaction: InvoiceSnapshot
    lateinit var oldCustomerTransaction: InvoiceSnapshot

    /**
     * Seeds two distinct transactions directly into the database, then launches MainActivity.
     * - Transaction 1: PEMBELIAN (purchase) from [supplierName], with PPN.
     * - Transaction 2: PENJUALAN (sale) to [customerName], with an existing installment —
     *   this is the richer precondition that lets the test verify no side-effects.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun userHasTwoDistinctTransaction(): Unit = runBlocking {
        val supplier = profileSeeder.run(supplierName, ProfileType.SUPPLIER)
        val customer = profileSeeder.run(customerName, ProfileType.CUSTOMER)
        val products = productSeeder.run(listOf(
            ProductEntity(name = existingProductA),
            ProductEntity(name = existingProductB),
        ))

        oldSupplierTransaction = invoiceSeeder.run(
            profile = supplier,
            date = transaction1Date,
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 3,
                    unitType = UnitType.PIECE,
                    product  = products[0],
                    price    = transaction1TotalPrice,
                )
            ),
            ppn = 11,
        )

        // ↓ Longest happy path: customer transaction already has an installment record.
        oldCustomerTransaction = invoiceSeeder.run(
            profile = customer,
            date = transaction2Date,
            invoiceItems = listOf(
                InvoiceItemSeed(
                    quantity = 2,
                    unitType = UnitType.PIECE,
                    product  = products[1],
                    price    = transaction2TotalPrice,
                )
            ),
            ppn = null,
            installmentSeed = InstallmentSeed(
                isPaidOff = false,
                items = listOf(
                    InstallmentItemSeed(
                        amount      = 25_000,
                        paymentDate = LocalDate.of(2020, 2, 15),
                    )
                ),
            ),
        )

        ActivityScenario.launch(MainActivity::class.java)
    }

    /**
     * Navigates to the add-transaction form and fills in a new, unique transaction
     * that exercises every optional field:
     *   - a brand-new product (newRegistration = true)
     *   - PPN enabled
     *   - installment payment type with two installment items   ← longest happy path
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun userAddNewUniqueTransaction() {
        transactionHistoryRobot.navigateToAddNewTransaction()
        fillTransactionSteps.fillForm(
            transactionType = TransactionType.PEMBELIAN,
            profileName = supplierName,
            isRegisterNewProfile = false,
            date = newTransactionDate,
            chooseProductActions = listOf(
                FormProductItem(
                    name            = newProductName,
                    quantity        = 4,
                    unitType        = UnitType.PIECE,
                    totalPrice      = newTransactionTotalPrice,
                    newRegistration = true,          // registers a brand-new product
                )
            ),
            ppn = 11,
            modifyPaymentSectionActions = listOf(
                ModifyPaymentSectionAction.SelectInstallmentPaymentType,
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount       = 40_000,
                    date         = LocalDate.of(2020, 2, 22),
                    paymentMedia = PaymentMedia.TRANSFER,
                ),
                ModifyPaymentSectionAction.AddNewInstallmentItem(
                    amount       = 30_000,
                    date         = LocalDate.of(2020, 2, 25),
                    paymentMedia = PaymentMedia.TRANSFER,
                ),
            ),
        )
    }

    /**
     * Opens the newly added transaction card and asserts every form field —
     * including the full installment payment details.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun theNewTransactionShouldAdded() {
        transactionHistoryRobot.waitAndClickTransactionCard(
            profileName                  = supplierName,
            pairTotalPriceAndProfileType = Pair(newTransactionTotalPrice, ProfileType.SUPPLIER),
            date                         = newTransactionDate,
        )
        transactionFormRobot.assertFormContent(
            transactionType = TransactionType.PEMBELIAN,
            date            = newTransactionDate,
            profileName     = supplierName,
            ppn             = 11,
            products        = listOf(
                ProductFormAssertData(
                    name     = newProductName,
                    price    = newTransactionTotalPrice,
                    quantity = 4,
                    unitType = UnitType.PIECE,
                )
            ),
            paymentTypeAssertion = PaymentTypeAssertionDto.Installment(
                isPaidOff = false,
                items     = listOf(
                    InstallmentItemAssertionDto(
                        paymentDate  = LocalDate.of(2020, 2, 22),
                        amount       = 40_000,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                    InstallmentItemAssertionDto(
                        paymentDate  = LocalDate.of(2020, 2, 25),
                        amount       = 30_000,
                        paymentMedia = PaymentMedia.TRANSFER,
                    ),
                ),
            ),
        )
        transactionFormRobot.backToPreviousScreen()
    }

    /**
     * Navigates back to the transaction history and asserts that both original seeded
     * transactions are still present and unmodified (no side-effects from the add action).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun previousTwoTransactionsShouldNotAffected() {
        transactionHistoryRobot.waitAndClickTransactionCard(oldSupplierTransaction)
        transactionFormRobot.assertFormContent(oldSupplierTransaction)
        transactionFormRobot.backToPreviousScreen()

        transactionHistoryRobot.waitAndClickTransactionCard(oldCustomerTransaction)
        transactionFormRobot.assertFormContent(oldCustomerTransaction)
    }
}
```

---

## 7. Known Issues & Conventions

| Issue                                                             | Status       | Notes                                                                                                                                                           |
|-------------------------------------------------------------------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `BaseEkonomisUiTest` vs `BaseGherkinInstrumentedTest` coexistence | ℹ️ Tech debt | Two parallel base classes. The `BaseEkonomisUiTest` is legacy. Prefer the DSL approach for new tests; consider migrating `feature/` integration tests gradually |