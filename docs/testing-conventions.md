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

## 5. Test Cases Naming

### 5.1 Mandatory naming convention

#### 5.1.1 JUnit 5

MUST have a descriptive JUnit 5 `@DisplayName` on every `@Test` and
`@ParameterizedTest`.

Use this format:

```text
When [triggering action or condition], and [some conditional action], then [expected outcome]
```

The `and [some conditional action]` part is optional. Use it only when the
scenario needs an additional condition.

Rules:

- `@DisplayName` is the authoritative test name.
- Function names MUST be descriptive camelCase and MUST NOT use names such as
  `test1`, `testToken`, or `saveToken`.
- Test names and `@DisplayName` values MUST be written in English.
- A class-level `@DisplayName` is optional and SHOULD describe the SUT or
  capability, not a single scenario.

Example:

```kotlin
@Test
@DisplayName("When valid credentials are submitted then login succeeds")
fun loginWithValidCredentials() {
    // ...
}

@Test
@DisplayName("When invalid credentials are submitted then authentication fails")
fun loginWithInvalidCredentials() {
    // ...
}
```

#### 5.1.2 JUnit 4

JUnit 4 tests MUST use backtick function names in the `When ... then ...`
format, together with `@file:Suppress("FunctionName")`.

Example:

```kotlin
@file:Suppress("FunctionName")

@Test
fun `When valid credentials are submitted then login succeeds`() {
    // ...
}
```

Do NOT add `@DisplayName` to JUnit 4 tests.

### 5.2 Examples

**Good**:

```kotlin
@Test
@DisplayName("When valid credentials are submitted then login succeeds")
fun loginWithValidCredentials() {
    // ...
}

@Test
@DisplayName("When invalid credentials are submitted then authentication fails")
fun loginWithInvalidCredentials() {
    // ...
}
```

**Bad**:

```kotlin
@Test
@DisplayName("Login success")
fun test1() {
    // ...
}

@Test
@DisplayName("Token")
fun saveToken() {
    // ...
}
```

**Bad**:

```kotlin
@Test
@DisplayName("When the response is received then the token is saved")
fun testTokenIsSaved() {
    // ...
}

@Test
@DisplayName("When the response is received then the session becomes valid")
fun testSessionBecomesValid() {
    // ...
}
```

The last example is bad because both tests describe the same trigger and the
same behavior. Both assertions MUST stay in one test case.

### 5.3 Parameterized tests

Use a parameterized test when the same trigger and expected outcome apply to
multiple input values.

Example:

```kotlin
@DisplayName("When the refresh token request receives a client error response then it returns an invalid refresh token error")
@ParameterizedTest(name = "When the response status code is {0}")
@ValueSource(ints = [400, 401])
fun returnsInvalidRefreshTokenErrorWhenStatusCodeIs(statusCode: Int) {
    // ...
}
```

Rules:

- `@DisplayName` describes the overall behavior and expected outcome.
- `@ParameterizedTest(name = ...)` describes the condition for each invocation.
- Do NOT use a parameterized test when different inputs produce different
  expected outcomes. Use separate test methods instead.

## Reference

- [Test Project Structure](./test-project-structure.md) for folder layout.
