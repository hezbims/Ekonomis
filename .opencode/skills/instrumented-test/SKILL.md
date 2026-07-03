---
name: instrumented-test
description: Use when writing or reviewing instrumented tests (Compose UI tests, database migration tests that run on device or emulator).
---

1. Read `docs/test-project-structure.md` and `docs/testing-conventions.md` first.
2. **UI assertions**: NEVER call `composeRule` directly in a test case. MUST use a robot. If no robot exists for the screen, create one.
3. **Utility abstraction**: Do NOT extract inline assertions into reusable assertions unless at 
   least two test cases need them. Read here for the guide when you need to create reusable assertions.
4. If unsure about structure or convention, ask the user.
