# Table of Contents
1. [Test Fixture](#test-fixture)
2. [Unit Test](#unit-test)
3. [Instrumented Test](#instrumented-test)

# Test Fixture

## Structure

<!--
Editable tree
com.hezapp.ekonomis
  app_test_utils
    screen-A
      data
        assert_attr
        assert_db
        mock
      application
        assert_attr
        mock
      presentation
        robot
          component
    screen-B
  base_test_utils
    rule
    seeder
    steps
    test_application
    other
  utils_aggregate
-->
```
com.hezapp.ekonomis
├── app_test_utils
│   ├── screen-A
│   │   ├── data
│   │   │   ├── assert_attr
│   │   │   ├── assert_db
│   │   │   └── mock
│   │   ├── application
│   │   │   ├── assert_attr
│   │   │   └── mock
│   │   └── presentation
│   │       └── robot
│   │           └── component
│   └── screen-B
├── base_test_utils
│   ├── rule
│   ├── seeder
│   ├── steps
│   ├── test_application
│   └── other
└── utils_aggregate
```
*Tree generated using [tree.nathanfriend.com](https://tree.nathanfriend.com/)*

## Explanation

Test fixture utilities are organized **by screen** under `app_test_utils`, mirroring the app's three-layer convention. Each screen folder keeps all fixture code co-located.

- **app_test_utils** — Root container for per-screen test utilities.
  - **`screen-A` / `screen-B`** — Each screen mirrors the three-layer structure:
    - `data/assert_attr` — Attribute assertions for data-layer models (Room entities, DTOs).
    - `data/assert_db` — Database assertions (e.g., check if an entity exists in DB).
    - `data/mock` — Mock implementations for data-layer dependencies.
    - `application/assert/attr` — Attribute assertions for application-layer models (use case results, DTOs).
    - `application/mock` — Mock implementations for application-layer dependencies.
    - `presentation/robot` — Page Object Model(s) for UI interaction and assertions.
    - `presentation/robot/component` — Reusable UI component interactors for the screen. Can be used inside robot
- **base_test_utils** — Utilities that are not tied to specific screens. Can be used across all test utilities or inside test case.
  - `rule` — JUnit rules used to perform actions before or after tests.
  - `seeder` — Responsible for seeding test data.
  - `steps` — Reusable business processes or steps that span multiple screens.
  - `test_application` — Custom Android `Application` used to start the app with an isolated Koin context.
  - `other` — Miscellaneous utilities that are useful across tests.
- **utils_aggregate** — Grouping multiple test utilities into a single class or object for easier injectability. 
Depends on `base_test_utils` and `app_test_utils` 


# Unit Test

## Structure

<!-- 
Editable tree with trailing '/' is off
com.hezapp.ekonomis/
  test_case/
    screen-A/
      application/
        get_something_use_case/
            BaseGetSomethingUseCaseTest.kt
            WhenUserDoThis.kt
            WhenUserDoThat.kt
      data/
      presentation/
        BaseScreenATest.kt
        WhenUserDoThis.kt
        WhenUserDoThat.kt
    screen-B/
      application/
        GetItemXUseCaseTest.kt
        GetItemYUseCaseTest.kt
      data/
      presentation
    core/
  utils/
    base_class/
-->
```
com.hezapp.ekonomis/
├── test_case/
│   ├── screen-A/
│   │   ├── application/
│   │   │   └── get_something_use_case/
│   │   │       ├── BaseGetSomethingUseCaseTest.kt
│   │   │       ├── WhenUserDoThis.kt
│   │   │       └── WhenUserDoThat.kt
│   │   ├── data/
│   │   └── presentation/
│   │       ├── BaseScreenATest.kt
│   │       ├── WhenUserDoThis.kt
│   │       └── WhenUserDoThat.kt
│   ├── screen-B/
│   │   ├── application/
│   │   │   ├── GetItemXUseCaseTest.kt
│   │   │   └── GetItemYUseCaseTest.kt
│   │   ├── data/
│   │   └── presentation
│   └── core/
└── utils/
    └── base_class/
```

Explanation:

There are two ways to structure test cases.
- `screen-A`: one unit is tested using multiple `when` scenarios across separate classes (multiple test cases in separate files).
- `screen-B`: one unit is tested in a single class (multiple test cases in one file).

# Instrumented Test

## Structure

<!-- 
Editable tree with trailing '/' is off
com.hezapp.ekonomis/
  acceptance_test/
  db_migration_test/
  other_ui_test/
  utils/
    base_class/
-->
```
com.hezapp.ekonomis/
├── acceptance_test/
├── db_migration_test/
├── other_ui_test/
└── utils/
    └── base_class/
```

---
> **Note:** This document describes the *target* test structure. The current folder structure is still being migrated and may not reflect this layout yet.

See also [app project structure](./app-project-structure.md) for the application source layout.