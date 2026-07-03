---
name: unit-test
description: Use when writing or reviewing unit tests (UI, Use Case, data layer tests that run on JVM via Robolectric).
---

1. Read `docs/test-project-structure.md` and `docs/testing-conventions.md` first.
2. **Presentation layer**: Mock all use case dependencies. NEVER call `composeRule` directly in a 
test case. MUST use a robot. If no robot exists for the screen, create one.
3. **Application layer**: Use in-memory Room database via Robolectric. Do NOT mock DAOs or the database.
4. **Utility abstraction**: Do NOT extract inline assertions into reusable assertions unless at
least two test cases need them. Read here for the guide when you need to create reusable assertions.
5. If unsure about structure or convention, ask the user.
