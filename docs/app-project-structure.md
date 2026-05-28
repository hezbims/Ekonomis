# Overview
This docs will explain the folder structure to create the app
To locate the app, [click this](/app/src/main/java/com/hezapp/ekonomis)

See also [test project structure](./test-project-structure.md) to
create automated test

# Structure Overview
<!--
Editable tree source (for future editing):
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
-->
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

This tree visualization is created using generated using [tree.nathanfriend.com](https://tree.nathanfriend.com/)


# Explanation
This app is not constructed using strict by-feature separation or 
strict clean architecture. Instead it rely on by-screen separation.
The folder named without underscore prefix is a screen except `core` folder.
Each screen can contains three layer, presentation, data (infrastructure), and application. 
No domain layer is included for simplicity (many business logic follows  transaction script pattern).

The item that can be placed in presentation layer includes :
- Screen and UI component
- Styling and theming
- View model
- UI data transfer object (DTO)
- UI validation/parsing/logic

The item that can be placed in application layer includes
- Use case and it results (DTO)
- infrastructure interface such as logging or monitoring

The item that can be placed in data layer includese 
- [DAO](#data-access-object-separation)
- Room Entity and Aggregate
- Query Result DTO
- infrastructure or third-party implementation


## Data Access Object Separation
There will be two types of Data access Object (DAO) in this app :
- Persistence DAO : to persist a room entity (table). One persistence
DAO must only responsible for persisting one Room Entity. For example :
`ProfileDao`, `ProductDao`, `InvoiceDao`, etc
- Read/Query DAO : for reading data. Read DAO is grouped based on
use case. for example : 
`GetProductPreviewReadDao`, `GetTransactionDetailsReadDao`, 
`GetTransactionPreviewsDao`, etc

## Other Folders
- `_koin_modules` : contains dependency injection
- `_testing_only` : not used in the app, but used in automated test
