# Test Utilities Judgement — Skills Guide

## Purpose
This skill focuses on critically evaluating the correctness and completeness of test utilities
(e.g. Seeder, Robot, assertion helpers) to prevent false-positive or misleading tests.

The goal is not only to make tests pass, but to ensure that passed tests actually verify the intended behavior.

## When to Use
Apply this skill when:
- Creating new test utilities
- Modifying existing test utilities
- Using shared utilities while defining acceptance tests

Test utilities are commonly located in the `app/testFixtures` directory.

## Core Principle
If a test utility is found to be incomplete, misleading, or not aligned with the system behavior,
you must **stop and report the issue immediately** instead of continuing the original test task.

Proceeding with invalid utilities risks creating tests that pass while hiding real defects.

## Common Red Flags

### 1. Unused Seeding Parameters
Example:
A `UserSeeder` accepts an `age` parameter, but the value is not persisted
despite `UserEntity` having an `age` column.

**Why this is dangerous:**
- Tests relying on `age` appear configurable but are silently ignored
- Assertions may pass due to default values, not real input

### 2. Incomplete Assertions in Helpers
Example:
A Robot method named `assertFullFormContent` only verifies a subset of fields
while ignoring others.

**Why this is dangerous:**
- The method name creates false confidence
- Missing fields can regress without any test failure

## Expected Mindset
When applying this skill, always ask:
- “If this field is incorrect, will the test fail?”
- “Does the utility name reflect what is truly being asserted or seeded?”
- “Am I trusting this helper because it is correct, or because it is reused?”

This skill emphasizes skepticism over blind reuse.
