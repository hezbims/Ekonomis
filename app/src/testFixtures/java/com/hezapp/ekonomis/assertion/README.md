# Assertion Guide

This folder mirrors the `app/src/main/java/com/hezapp/ekonomis` package structure.
Each model class under assertion mirrors a corresponding production class.

## When to add an assertion model

Only create an assertion model when the assertion logic is **reused across multiple test cases**.
If only one test needs it, inline the assertions directly.

## Handling optional attributes

If you need a similar assertion model but want to skip checking some attribute:

- **Use a default parameter** (e.g., `val foo: Type = someDefault`), or
- **Create a new assertion model** without that attribute.

Never introduce a wrapper type like `Ignorable<T>` just to mark attributes as optionally asserted — it adds needless complexity.

## Requirements

- Both the assertion model and the class it asserts **must be `data class`**.
- The assertion model must extend `AssertionModel<T>` and implement `matches(actual: T)`.

