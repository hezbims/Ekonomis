# Overview

This document explains the folder structure of the app.  
To locate the app, [click here](/app/src/main/java/com/hezapp/ekonomis).

See also [test project structure](./test-project-structure.md) for automated tests.

## Structure Overview

[//]: # (
Editable Tree for Future Editing
com.hezapp.ekonomis
  _testing_only
  _koin_modules
  add_or_update_transaction
    application
    data
    presentation
  core
  edit_product_name_dialog
  product_preview
  product_detail
  transaction_history
)

```
com.hezapp.ekonomis
├── _testing_only
├── _koin_modules
├── add_or_update_transaction
│   ├── application
│   ├── data
│   └── presentation
├── core
├── edit_product_name_dialog
├── product_preview
├── product_detail
└── transaction_history
```

*Tree generated using [tree.nathanfriend.com](https://tree.nathanfriend.com/)*

## Structure Explanation

This app is not constructed using strict by-feature separation or strict clean architecture.
Instead, it relies on by-screen separation.  
Folders without an underscore prefix represent a screen, except the `core` folder.  
Each screen can contain three layers:

- **presentation** – Screen and UI components, styling, theming, view models, UI DTOs, UI
  validation/parsing/logic.
- **application** – Use cases and their results (DTOs), infrastructure interfaces (e.g., logging,
  monitoring).
- **data** – DAOs, Room entities and aggregates, query result DTOs, infrastructure or third‑party
  implementations.

## Rules

### Data Transfer Object (DTO)

- Presentation layer **can depend on application DTOs** *only if all fields
  are used in the UI*.
    - If only some fields are needed, create a **new DTO specifically for the UI**.
    - Exposing unused data to the UI is considered an **anti-pattern**.
- When converting DTO between layer (e.g. application DTO to presentation DTO):
    - Do **not** create a separate mapper class.
    - Use a `companion object` in the UI DTO instead.
- Presentation layer DTO **must not depend on any data layer DTO**.
- Application layer use case **must only return application layer DTO**.

### Data Access Object (DAO)

There are two types of DAO in this app:

- **Persistence DAO** – responsible for persisting one Room entity (table).  
  Examples: `ProfileDao`, `ProductDao`, `InvoiceDao`.
- **Read/Query DAO** – grouped by use case for reading data.  
  Examples: `GetProductPreviewReadDao`, `GetTransactionDetailsReadDao`, `GetTransactionPreviewsDao`.

## Other Folders

- `_koin_modules` – dependency injection modules.
- `_testing_only` – not used in the app, but used in automated tests.