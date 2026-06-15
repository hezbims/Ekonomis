# Table of Contents
1. [Test Fixture](#test-fixture)
2. [Unit Test](#unit-test)
3. [Instrumented Test](#instrumented-test)

# Test Fixture

## Structure

[//]: # (
Editable  tree
com.hezapp.ekonomis
  robot
    _dto
    _interactor
    screen-A
      _dto
      _interactor
    screen-B
    screen-C
  rule
  steps
  test_application
  seeder
  other
)
```
com.hezapp.ekonomis
├── robot
│   ├── _dto
│   ├── _interactor
│   ├── screen-A
│   │   ├── _dto
│   │   └── _interactor
│   ├── screen-B
│   └── screen-C
├── rule
├── steps
├── test_application
├── seeder
└── other
```
*Tree generated using [tree.nathanfriend.com](https://tree.nathanfriend.com/)*

## Explanation
- **robot** - Page Object Model used to control a screen and assert its state. A robot can include multiple isolated component interactors under the `_interactor` folder.
- **rule** - JUnit rules used to perform actions before or after tests.
- **steps** - Reusable business processes or steps that span multiple screens.
- **test_application** - Custom Android `Application` used to start the app with an isolated Koin context.
- **seeder** - Responsible for seeding test data.
- **other** - Miscellaneous utilities that are useful across tests.


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