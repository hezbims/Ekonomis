## When to add an attribute assertion

Only create an attribute assertion model when the assertion logic is **reused across multiple test cases**.
If only one test needs it, inline the assertions directly.

## Where to Place
Attribute assertions can be placed mirroring the location of the model it asserts in the app module.
For example, when the original model is in `product_detail/application`, then the attribute
assertion model will be placed inside the test fixture folder `app_test_utils/product_detail/application/assert_attr`

## Handling optional attributes

If you need a similar assertion model but want to skip checking some attribute:

- **Create a new assertion model** without that attribute.

Never introduce a wrapper type like `Ignorable<T>` just to mark attributes as optionally asserted — it adds needless complexity.

## Requirements

- Both the assertion model and the class it asserts **must be `data class`**.
- The assertion model must extend `AssertionModel<T>` and implement `matches(actual: T)`.

## Example

Model Definition:
```kotlin
data class ExpectedProductTransaction(
    val price: Int,
    val quantity: Int,
    val unitType: UnitType,
    val date: Long,
    val ppn: Int?,
    val profileName: String,
) : AssertionModel<ProductTransaction>() {

    override fun matches(actual: ProductTransaction) {
        verifyAll(
            assertEquals(price, actual.price),
            assertEquals(quantity, actual.quantity),
            assertEquals(unitType, actual.unitType),
            assertEquals(date, actual.date),
            assertEquals(ppn, actual.ppn),
            assertEquals(profileName, actual.profileName),
        )
    }
}
```

Using the assertion:
```kotlin
// actualProductTransaction is typeof ProductTransaction
actualProductTransaction.matches(ExpectedProductTransaction(
    date = dateEpochMillis,
    unitType = unitType,
    quantity = quantity,
    price = price,
    ppn = null,
    profileName = "buyer-1",
))
```