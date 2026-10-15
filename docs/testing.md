# GardenConnect - Automated Testing & Verification Guide

## 1. Automated Testing Philosophy

GardenConnect uses a layered testing strategy designed to provide high test coverage for business logic, input validation, cryptographic operations, access control rules, and concurrency mechanisms without requiring a running MySQL database server during the standard build pipeline.

All unit tests are implemented using **JUnit 5 (Jupiter)** and **Mockito 5**.

```
Total Automated Unit Tests: 43
Failures: 0
Errors: 0
Execution Time: ~1.5 - 2.0s
```

---

## 2. Test Suite Breakdown

### 2.1 `ValidationUtilTest` (24 Tests)
Validates core input sanitization and verification routines:
- **Email Validation**: Tests valid standards (plus-addressing, subdomains) and rejects missing domains, consecutive dots, whitespace, and empty strings.
- **Username Rules**: Verifies length constraints (3-30 chars), allowed characters (`[a-zA-Z0-9_]`), and rejects whitespace and special symbols.
- **Password Strength**: Enforces complexity rules (minimum 8 characters, at least 1 uppercase, 1 lowercase, 1 number, and 1 special symbol).
- **Safe Parsing**: Validates fallback behaviors for non-numeric, negative, and null inputs when parsing integers and Long IDs.

### 2.2 `PasswordHasherTest` (4 Tests)
Ensures security compliance of BCrypt password hashing:
- **Format Verification**: Asserts that hashes adhere to the 60-character BCrypt structure with standard `$2a$` cost prefix.
- **Correct Match**: Asserts `checkPassword(candidate, hash) == true` for genuine credentials.
- **Wrong Password Defense**: Confirms that incorrect candidates fail verification.
- **Tamper & Null Safety**: Verifies that null, empty, or malformed hashes return `false` without throwing uncaught exceptions.

### 2.3 `ProjectServiceTest` (5 Tests)
Tests personal gardening project rules:
- **Auto-Completion**: Setting project progress to `100%` automatically transitions the status to `ProjectStatus.COMPLETED`.
- **Range Enforcement**: Verifies that progress values `< 0` or `> 100` throw `ValidationException`.
- **Strict Ownership Isolation**: Verifies that attempting to access, update, or delete another gardener's project triggers an `UnauthorizedActionException`.
- **Resource Not Found**: Verifies that requests for missing project IDs throw `ResourceNotFoundException`.

### 2.4 `ModerationServiceTest` (4 Tests)
Tests content review workflow:
- **Approval Transition**: Approving content updates moderation status to `APPROVED` and records an asynchronous audit log.
- **Rejection with Reason**: Rejecting content updates status to `REJECTED`, records the documented reason, and registers the reviewing administrator.
- **Reason Validation**: Confirms that empty or whitespace-only rejection reasons throw `ValidationException`.

### 2.5 `PaginationUtilTest` (4 Tests)
Validates generic pagination math:
- **Page Calculations**: Verifies that 25 items at 10 items/page yields 3 pages, and 0 items yields 0 pages.
- **Navigation Bounds**: Tests `hasPrevious()`, `hasNext()`, and edge bounds on first and last pages.
- **SQL Offset Calculation**: Verifies that page 1 yields offset 0, page 2 yields 10, and negative pages default safely to offset 0.

### 2.6 `AsyncActivityProcessorTest` (2 Tests)
Tests multithreading and thread synchronization:
- **Producer-Consumer Verification**: Tests that multiple tasks submitted concurrently to the bounded queue are polled and persisted by the background worker thread using `CountDownLatch`.
- **Atomic Telemetry**: Asserts that `totalEnqueued` and `totalPersisted` increment atomically, and `ConcurrentHashMap` correctly tracks action frequency.
- **Graceful Lifecycle Shutdown**: Confirms that calling `shutdown()` rejects subsequent submissions and drains remaining queue items cleanly.

---

## 3. Running Automated Tests

To execute the test suite from the project root:

```bash
# Run all unit tests
mvn test

# Run a specific test class
mvn test -Dtest=ValidationUtilTest

# Run tests and generate the WAR deployment package
mvn clean package
```
