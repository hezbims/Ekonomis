# Testing Conventions

## 1. Robot Pattern

- **All** UI interaction and assertion **must** go through a robot.
- Calling `composeRule` directly in a test class is **forbidden**.

## 2. Test Utility Abstraction

Do **not** create an attribute assertion or DB assertion utility unless at least **two** test cases need it. If only one test case uses a given assertion, keep it inline.

## 3. Unit Test — Presentation Layer

- **Mock** all use case dependencies when testing ViewModels / presentation logic.
- Focus on verifying state transformations, event handling, and navigation.

## 4. Unit Test — Application Layer

- Use an **in-memory Room database** backed by **Robolectric**.
- Do **not** mock DAOs or the database itself. The goal is to test use case logic against a real (in-memory) database.

## Reference

- [Test Project Structure](./test-project-structure.md) for folder layout.
