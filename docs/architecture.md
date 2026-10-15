# GardenConnect - Architectural Design Document

## 1. Overview & Architectural Philosophy

**GardenConnect** is designed and implemented strictly following the classic **Model-View-Controller (MVC)** architectural pattern using pure **Jakarta EE (Servlet 6.0, JSP 3.0, JSTL)** and standard **JDBC** connectivity. It purposefully avoids heavy runtime frameworks like Spring Boot to demonstrate foundational mastery of enterprise Java, object-oriented design principles, low-level database connection pooling, thread-safe concurrency, and web security.

```
       [ Client Browser (HTML5 / Bootstrap 5 / JS / Chart.js) ]
                               │
                      HTTP / HTTPS Requests
                               ▼
        ┌────────────────────────────────────────────────────────┐
        │                 Servlet Filter Pipeline                │
        │  1. EncodingFilter (UTF-8 enforce)                     │
        │  2. MaintenanceFilter (MySQL dynamic toggle)           │
        │  3. AuthenticationFilter (Session check)               │
        │  4. AuthorizationFilter (RBAC: ADMIN vs GARDENER)      │
        │  5. CsrfFilter (Constant-time token validation)        │
        └────────────────────────┬───────────────────────────────┘
                                 │
                                 ▼
                     [ Servlet Controller Layer ]
                     (Extends BaseController)
                                 │
                     Dispatches to Business Logic
                                 ▼
                      [ Service Layer (BLL) ]
               (Validation, Transactions, Moderation)
                    │                         │
            Asynchronous Event          Data Access Operations
                    ▼                         ▼
         [ ActivityProcessor ]          [ DAO Layer ]
     (Queue + Worker Thread Pool)     (JDBC + PreparedStatements)
                    │                         │
                    ▼                         ▼
        [ Audit Logs Table ]       [ MySQL 8.0 Database ]
                                              │
                      Server-Side Forwarding  ▼
                     [ JSP & JSTL View Layer ]
```

---

## 2. Layered Architecture Breakdown

### 2.1 View Layer (JSP & JSTL)
- **Role**: Presentation and UI rendering.
- **Technology**: Jakarta Server Pages 3.0, Jakarta Standard Tag Library (`jakarta.tags.core`, `jakarta.tags.fmt`), Bootstrap 5, Bootstrap Icons, Chart.js.
- **Principle**: Zero database access or direct business logic in JSPs. All dynamic data is supplied through request attributes set by controllers.
- **Components**:
  - `src/main/webapp/WEB-INF/views/fragments/`: Reusable header, navigation bar, alerts, footer, and role-specific sidebars.
  - `src/main/webapp/WEB-INF/views/auth/`: Login and registration views.
  - `src/main/webapp/WEB-INF/views/gardener/`: Dashboard, profile management, personal tips, discussions, and project CRUD.
  - `src/main/webapp/WEB-INF/views/admin/`: System analytics, user administration, moderation queue, settings, and audit trails.
  - `src/main/webapp/WEB-INF/views/errors/`: Standard HTTP error pages (403 Forbidden, 404 Not Found, 500 Internal Error, Maintenance Mode).

### 2.2 Controller Layer (Jakarta Servlets)
- **Role**: HTTP request handling, session retrieval, request parameter parsing, delegating to services, and selecting views.
- **Hierarchy**: All controllers inherit from `BaseController` which provides standard helper methods:
  - `forward()`, `redirect()`, `sendForbidden()`, `sendNotFound()`, `sendInternalError()`
  - `setFlashSuccess()`, `setFlashError()`, `getAuthenticatedUser()`
- **Route Segregation**:
  - Public routes: `HomeController`, `AuthController`, `TipController`, `DiscussionController`
  - Gardener routes: `GardenerDashboardController`, `GardenerProfileController`, `GardenerTipController`, `GardenerDiscussionController`, `GardenerProjectController`
  - Admin routes: `AdminDashboardController`, `AdminUserController`, `AdminModerationController`, `AdminSettingsController`, `AdminActivityController`

### 2.3 Service Layer (Business Logic Layer)
- **Role**: Validates inputs, coordinates domain rules, enforces access boundaries, interacts with multiple DAOs, and triggers audit events.
- **Contract-Based Design**: Every service defines an explicit Java interface (`UserService`, `TipService`, `ProjectService`, `ModerationService`, etc.) implemented by its respective `*ServiceImpl`.
- **Dependency Inversion**: Services declare dependencies on DAO interfaces and the `ActivityProcessor` interface, allowing full unit testing with mock implementations.

### 2.4 Data Access Object (DAO) Layer
- **Role**: Encapsulates all SQL execution, PreparedStatement binding, and ResultSet-to-Domain object mapping.
- **BaseDAO Generic Pattern**: `BaseDAO<T, ID>` specifies standard CRUD signatures (`create`, `findById`, `findAll`, `update`, `delete`).
- **Connection Management**: Leverages `DatabaseConfig` which manages a high-performance **HikariCP** connection pool. Every JDBC operation uses try-with-resources blocks (`try (Connection conn = ...; PreparedStatement stmt = ...)`) to prevent connection leaks.

---

## 3. Concurrency & Asynchronous Processing

To satisfy academic multithreading requirements and prevent web thread starvation during heavy logging:
1. **Producer-Consumer Pattern**:
   - `AsyncActivityProcessor` implements `ActivityProcessor`.
   - Uses a bounded `LinkedBlockingQueue<ActivityLog>` with a default capacity of 2,000 items.
   - When user actions occur (e.g. login, project creation, moderation approval), the controller/service submits an `ActivityLog` to the queue without waiting for MySQL disk I/O.
2. **Dedicated Worker Thread**:
   - Managed via a single-thread `ExecutorService` with a custom `ThreadFactory` (`GardenConnect-AsyncLogger`).
   - Polls entries and persists them in batches to the `activity_logs` table.
3. **Atomic Telemetry Counters**:
   - `AtomicInteger totalEnqueued`, `totalPersisted`, `totalDropped`.
   - `ConcurrentHashMap<String, AtomicInteger> actionCounterMap` monitors action frequency across concurrent threads without locks.
4. **Lifecycle Hooks**:
   - `AppContextListener` initializes the connection pool and starts background schedulers upon web application deployment.
   - Shuts down executors with a 5-second drain window and closes HikariCP on context destruction to prevent JVM thread leaks.

---

## 4. Security Architecture

1. **Password Hashing**:
   - Built on `jBCrypt` with cost factor 12. Plaintext passwords are never logged or stored.
2. **Session Authentication & Privilege Escalation Prevention**:
   - `AuthenticationFilter` intercepts protected paths (`/gardener/*`, `/admin/*`).
   - `AuthorizationFilter` verifies that only users with `Role.ADMIN` can invoke `/admin/*` routes.
   - Self-promotion is structurally impossible: the registration form accepts no role parameter and always assigns `Role.GARDENER`.
3. **Cross-Site Request Forgery (CSRF) Protection**:
   - `CsrfTokenManager` generates cryptographically secure, 32-byte session-bound tokens.
   - `CsrfFilter` intercepts state-changing HTTP methods (`POST`, `PUT`, `DELETE`).
   - Constant-time comparison (`MessageDigest.isEqual`) prevents timing attacks.
4. **SQL Injection Defense**:
   - Every single database query uses `PreparedStatement` with typed parameter binding (`setString`, `setLong`, `setInt`).
   - Zero dynamic string concatenation for user-supplied SQL values.
5. **Cross-Site Scripting (XSS) Prevention**:
   - All freeform user text is sanitized using `InputSanitizer.escapeHtml()`.
   - JSP output rendering utilizes `<c:out value="${...}" />` to encode HTML entities by default.

---

## 5. Design Patterns Applied

| Design Pattern | Implementation in GardenConnect | Academic Value |
|---|---|---|
| **Model-View-Controller (MVC)** | Servlets (Controller), JSP/JSTL (View), Domain Models (Model) | Architecture & Separation of Concerns |
| **Data Access Object (DAO)** | `UserDAO`, `TipDAO`, `GardeningProjectDAO` + JDBC | Decoupled Persistence & Abstraction |
| **Generics & Base Interface** | `BaseDAO<T, ID>`, `PaginationUtil<T>` | Type Safety, Code Reuse |
| **Producer-Consumer** | `AsyncActivityProcessor` with `BlockingQueue` and worker thread | Multithreading & Concurrency |
| **Singleton (Thread-Safe)** | `DatabaseConfig.getDataSource()`, `AsyncActivityProcessor.getInstance()` | Resource efficiency & shared pools |
| **Chain of Responsibility** | `EncodingFilter` -> `MaintenanceFilter` -> `AuthFilter` -> `CsrfFilter` | Servlet Filter Pipeline |
| **Dependency Inversion** | Services depend on DAO interfaces and `ActivityProcessor` interface | Testability & Loose Coupling |
