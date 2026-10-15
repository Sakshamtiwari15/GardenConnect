# GUVI–HCL Academic Evaluation Rubric Mapping (33 Marks Total)

> **Course / Capstone**: Full Stack Java Development  
> **Evaluation Framework**: GUVI–HCL Industry & Academic Project Assessment  
> **Total Score Available**: 33 Marks  
> **Project Name**: GardenConnect (Online Gardening Community Platform)  

This document serves as an exhaustive evaluation and grading guide for faculty evaluators, industry assessors, and technical reviewers. It links each criterion of the **33-mark GUVI–HCL Academic Rubric** directly to the underlying architecture, interfaces, classes, and SQL tables in GardenConnect.

---

## 1. Executive Evaluation Matrix (33 Marks Total)

| Rubric Criterion | Marks | Evaluated Domain | Primary Source Files & Interfaces | Concrete Evidence & Verification |
|---|:---:|---|---|---|
| **1. Problem Understanding & Solution Design** | **8** | Architecture, Requirements & Domain Modeling | • [architecture.md](file:///c:/shaurya/Online%20Gardening%20Community%20Platform/docs/architecture.md)<br>• [database-schema.md](file:///c:/shaurya/Online%20Gardening%20Community%20Platform/docs/database-schema.md)<br>• `com.gardenconnect.controller.*`<br>• `com.gardenconnect.service.*`<br>• `src/main/webapp/WEB-INF/views/` | 1. 3-tier MVC architecture with pure Jakarta Servlets 6.0 and JSP 3.0.<br>2. Real-world community workflows (personal crop tracker, moderated tip authoring, community discussion forum).<br>3. Relational database with 9 normalized tables in 3NF with foreign key cascading.<br>4. Custom botanical UI tokens with Bootstrap 5 and Chart.js analytics. |
| **2. Core Java Concepts** | **10** | OOP, Collections, Generics, Exceptions, Concurrency | • `BaseDAO<T, ID>`<br>• `PaginationUtil<T>`<br>• `BaseController`<br>• `ActivityProcessor`<br>• `AsyncActivityProcessor`<br>• `GardenConnectException` | 1. Strict encapsulation across all domain POJOs with guarded setters and defensive copies.<br>2. 14 servlet controllers inherit from abstract `BaseController`.<br>3. Interface-driven polymorphism (`Service` $\rightarrow$ `DAO` interfaces) adhering to DIP.<br>4. Custom exception hierarchy with structured error maps and try-with-resources.<br>5. Producer-Consumer multithreading with `LinkedBlockingQueue` and `ExecutorService`.<br>6. 43 passing automated JUnit 5 / Mockito unit tests. |
| **3. Database Integration (JDBC)** | **8** | Persistence, Pooling, Transactions & Query Safety | • `database/schema.sql`<br>• `database/seed.sql`<br>• `DatabaseConfig.java`<br>• `com.gardenconnect.dao.*`<br>• `com.gardenconnect.dao.impl.*` | 1. HikariCP connection pool configuration (min idle: 5, max pool: 20).<br>2. 100% `PreparedStatement` usage with typed parameter binding (zero string concatenation).<br>3. Try-with-resources resource safety on `Connection`, `PreparedStatement`, `ResultSet`.<br>4. Manual ACID transaction management (`setAutoCommit(false)`, `commit()`, `rollback()`).<br>5. Database-level pagination using `LIMIT ? OFFSET ?`. |
| **4. Servlets & Web Integration** | **7** | HTTP Protocol, Filters, Session Management & JSPs | • `web.xml`<br>• `com.gardenconnect.filter.*`<br>• `com.gardenconnect.listener.*`<br>• `com.gardenconnect.controller.*`<br>• `src/main/webapp/WEB-INF/views/` | 1. Native Jakarta Servlet 6.0 (`jakarta.servlet.*`) deployed on Apache Tomcat 10.1.<br>2. 5-stage filter chain: UTF-8 encoding, maintenance mode gate, session authentication, role RBAC, and CSRF token verification.<br>3. HTTP session management with 30-min timeouts and complete invalidation on logout.<br>4. 29 JSP views using JSTL tags (`<c:forEach>`, `<c:if>`, `<c:out>`) with zero raw scriptlets (`<% ... %>`).<br>5. Post/Redirect/Get (PRG) pattern with session flash messaging. |
| **Total Marks** | **33** | | | **Full Fulfillment of All Criteria** |

---

## 2. In-Depth Criterion-by-Criterion Evaluation Guide

### Criterion 1: Problem Understanding & Solution Design (8 Marks)

#### A. Problem Analysis & Domain Workflow (3 Marks)
- **Problem Addressed**: Solves fragmentation in personal gardening project tracking and tackles misinformation in online gardening communities.
- **Gardener Workflow**:
  - Personal Project Tracker (`/gardener/projects`): Allows gardeners to plan crops, set target harvest dates, input notes, and adjust a real-time completion percentage ($0\%$ to $100\%$).
  - Auto-Completion Rule: Reaching $100\%$ automatically transitions status to `COMPLETED`; lowering below $100\%$ transitions status to `IN_PROGRESS`.
  - Multi-Tenant Isolation: Enforced in `ProjectServiceImpl.java` where attempts to view, edit, or delete another user's project throw `UnauthorizedActionException` (HTTP 403).
  - Tip Submission Workflow (`/gardener/tips`): Submissions enter a `PENDING` state and require administrative approval before appearing publicly.
- **Administrator Governance**:
  - Content Moderation Queue (`/admin/moderation`): Review pending tips and topics; one-click approval or rejection requiring written feedback rationale.
  - User Directory (`/admin/users`): Role promotion/demotion, account activation/deactivation, and safe deletion (cannot delete self; cannot demote the last remaining admin).
  - Dynamic Settings (`/admin/settings`): Runtime key-value configuration stored in MySQL (`system_settings`) affecting maintenance mode, registration, upload limits, and page size.

#### B. Architectural Separation & MVC Layers (3 Marks)
- **Directory Separation**:
  - Controllers: `com.gardenconnect.controller.*` (servlets handling HTTP requests, parameters, and redirects).
  - Services: `com.gardenconnect.service.*` (domain rules, transactions, and access authorization).
  - Data Access Objects: `com.gardenconnect.dao.*` (SQL query encapsulation and entity hydration).
  - Models: `com.gardenconnect.model.*` (domain entities and enums).
  - Filters: `com.gardenconnect.filter.*` (cross-cutting request interception).
  - Views: `src/main/webapp/WEB-INF/views/` (server-rendered templates inaccessible directly via URL).

#### C. Relational Data Modeling (2 Marks)
- **Normalized Schema**: 9 tables in Third Normal Form (3NF) in `database/schema.sql`:
  - `users`: Core profile, credentials, and role.
  - `tip_categories` & `discussion_categories`: Standardized taxonomy classifications.
  - `tips`: Horticultural advice with moderation lifecycle (`PENDING`, `APPROVED`, `REJECTED`).
  - `discussions` & `comments`: Threaded forum conversations with cascading deletes.
  - `gardening_projects`: Personal crop tracking with CHECK constraint (`progress_percentage BETWEEN 0 AND 100`).
  - `system_settings`: Key-value runtime platform configuration.
  - `activity_logs`: Asynchronous security audit trail.

---

### Criterion 2: Core Java Concepts (10 Marks)

#### A. Object-Oriented Principles (OOP) (3 Marks)
1. **Encapsulation**:
   - `User.java`, `Tip.java`, `GardeningProject.java`: All member variables are strictly `private`.
   - Mutator methods validate state invariants. Sensitive attributes (such as `password_hash`) are protected and never exposed to views.
2. **Inheritance**:
   - All 14 servlet controllers inherit from `com.gardenconnect.controller.BaseController`, which extends `HttpServlet`.
   - `BaseController` centralizes `forward()`, `redirect()`, `setFlashSuccess()`, `setFlashError()`, `getAuthenticatedUser()`, and `sendError()`.
   - Custom exceptions inherit from `com.gardenconnect.exception.GardenConnectException`.
3. **Polymorphism & Abstraction**:
   - Higher-level services depend on abstract interfaces (`UserDAO`, `TipDAO`, `ProjectDAO`, `ActivityProcessor`) rather than concrete implementations, fulfilling the **Dependency Inversion Principle (DIP)**.
   - Controllers override `doGet()` and `doPost()` to implement RESTful routing.

#### B. Generics & Collections Framework (2 Marks)
1. **Generic Contracts**:
   - `BaseDAO<T, ID>`: Parameterized interface for generic CRUD operations across any entity and ID type:
     - `com.gardenconnect.dao.BaseDAO<T, ID>`
   - `PaginationUtil<T>`: Generic model encapsulating paginated result sets (`List<T> items`, `int currentPage`, `int totalPages`, `long totalItems`, `int pageSize`).
2. **Collections Usage**:
   - `List` / `ArrayList`: Entity collections across all DAO queries.
   - `Map` / `HashMap`: Form validation error dictionaries (`Map<String, String> errors`) and KPI metric mappings (`Map<String, Object> metrics`).
   - `BlockingQueue`: `LinkedBlockingQueue<ActivityLog>` in `AsyncActivityProcessor`.
   - `ConcurrentMap`: `ConcurrentHashMap<String, AtomicInteger>` for lock-free action frequency tracking.

#### C. Exception Handling Architecture (2 Marks)
1. **Custom Exception Hierarchy**:
   - `GardenConnectException` (Root RuntimeException).
   - `ValidationException` (HTTP 400 Bad Request, holding field-level error maps).
   - `UnauthorizedActionException` (HTTP 403 Forbidden, for unauthorized role or cross-tenant access).
   - `ResourceNotFoundException` (HTTP 404 Not Found, for missing IDs).
   - `DatabaseOperationException` (HTTP 500 Internal Error, wrapping checked `SQLException`s).
2. **Resource Management**: Universal try-with-resources blocks guarantee statement and connection closure.
3. **User-Friendly Error Pages**: Web container maps HTTP 403, 404, and 500 error codes in `web.xml` to dedicated JSPs (`403.jsp`, `404.jsp`, `500.jsp`), preventing stack trace exposure.

#### D. Multithreading & Concurrency (2 Marks)
1. **Producer-Consumer Telemetry Engine**:
   - `com.gardenconnect.concurrency.AsyncActivityProcessor` implements `ActivityProcessor`.
   - Bounded `LinkedBlockingQueue<ActivityLog>` with capacity 2,000 prevents memory exhaustion.
   - Background worker managed by `ExecutorService` with a named `ThreadFactory` (`GardenConnect-AsyncLogger-%d`).
2. **Atomic Synchronization**:
   - `AtomicInteger` counters (`totalEnqueued`, `totalPersisted`, `totalDropped`) record non-blocking metrics across concurrent requests.
   - `AtomicBoolean` manages startup and shutdown coordination.
3. **Graceful Lifecycle Shutdown**:
   - `AppContextListener.contextDestroyed()` calls `processor.shutdown()`, providing a 5-second `awaitTermination` grace window to flush remaining queued logs before Tomcat tears down the context.

#### E. Automated Unit Testing (1 Mark)
- 43 automated unit tests using **JUnit 5 (Jupiter)** and **Mockito 5**:
  - `PasswordHasherTest`: 6 tests
  - `ProjectServiceTest`: 10 tests
  - `ModerationServiceTest`: 9 tests
  - `ValidationUtilTest`: 12 tests
  - `AsyncActivityProcessorTest`: 6 tests
- Run and verify via `mvn clean test`.

---

### Criterion 3: Database Integration (JDBC) (8 Marks)

#### A. Connection Pooling via HikariCP (2 Marks)
- Managed by `com.gardenconnect.config.DatabaseConfig`.
- Zero raw `DriverManager` connections; uses a configured singleton `HikariDataSource`.
- Pool parameters: maximum pool size: 20, minimum idle: 5, connection timeout: 10,000ms, idle timeout: 300,000ms.
- Gracefully shut down in `AppContextListener`.

#### B. PreparedStatement Parameterization & SQL Safety (2 Marks)
- 100% of SQL statements across all DAO implementations use `PreparedStatement` with typed parameter setters (`setString`, `setLong`, `setInt`, `setDate`, `setTimestamp`).
- Zero dynamic SQL string concatenation, eliminating SQL injection vulnerabilities.

#### C. Database Transactions (ACID Compliance) (2 Marks)
- Multi-step operations leverage explicit transaction demarcations:
  - `conn.setAutoCommit(false)`
  - Execute grouped SQL statements sharing the same connection.
  - `conn.commit()` on success.
  - `conn.rollback()` in `catch (SQLException e)` block to guarantee atomic consistency.

#### D. Database Pagination & Performance Tuning (2 Marks)
- Queries returning list results (`TipDAOImpl`, `UserDAOImpl`, `DiscussionDAOImpl`, `ActivityLogDAOImpl`) utilize database-level pagination:
  ```sql
  SELECT * FROM tips WHERE moderation_status = 'APPROVED' 
  ORDER BY created_at DESC LIMIT ? OFFSET ?;
  ```
- Indexes defined on foreign keys, status flags, and timestamps (`idx_tips_status_date`, `idx_users_role`, `idx_logs_created_at`).

---

### Criterion 4: Servlets & Web Integration (7 Marks)

#### A. Jakarta Servlet 6.0 & Web Standards (2 Marks)
- Native implementation of `jakarta.servlet.http.HttpServlet` compatible with Apache Tomcat 10.1+.
- Request routing handled via `@WebServlet` annotations and `web.xml` mappings.
- Clean separation between business servlets and server-rendered views.

#### B. Servlet Filter Pipeline (2 Marks)
Configured in `src/main/webapp/WEB-INF/web.xml`:
1. `EncodingFilter`: Forces UTF-8 character encoding on all requests and responses.
2. `MaintenanceFilter`: Inspects dynamic `system_settings` table for `maintenance_mode = true`; redirects non-admin traffic to `maintenance.jsp`.
3. `AuthenticationFilter`: Intercepts protected paths (`/gardener/*`, `/admin/*`) and checks for an active session user; unauthenticated requests redirect to `/login`.
4. `AuthorizationFilter`: Verifies user role; prevents gardeners from accessing `/admin/*` routes (HTTP 403).
5. `CsrfFilter`: Validates CSRF tokens on all state-changing HTTP `POST` requests.

#### C. Session Management & Security (2 Marks)
- HTTP sessions configured with 30-minute idle timeouts in `web.xml`.
- Complete session invalidation on logout (`req.getSession().invalidate()`).
- Session hijacking mitigation via session recreation upon authentication.
- Cryptographic CSRF tokens generated using `SecureRandom` and verified using constant-time comparison (`MessageDigest.isEqual`).

#### D. JSP Views & JSTL Implementation (1 Mark)
- 29 JSP view files stored under `/WEB-INF/views/` (protected from direct client URL browsing).
- 100% scriptlet-free: zero raw `<% ... %>` scriptlets.
- Exclusively uses JSTL 3.0 tags:
  - `<c:forEach>` for iteration.
  - `<c:if>` and `<c:choose>` for conditional rendering.
  - `<c:out value="..." />` for automatic HTML escaping against XSS.
- Reusable JSP fragments: `header.jsp`, `navbar.jsp`, `footer.jsp`, `sidebar.jsp`, `alerts.jsp`.
- Post/Redirect/Get (PRG) pattern with session flash messaging.

---

## 3. Evaluator Demonstration Script

Follow this step-by-step checklist to evaluate GardenConnect in a live examination:

1. **Build and Test Verification**:
   ```bash
   mvn clean test
   ```
   *Expected Result*: All 43 unit tests pass with 0 failures and 0 errors.
2. **Inspect Database & Relational Integrity**:
   - Open MySQL client and run:
     ```sql
     USE gardenconnect;
     SHOW TABLES;
     SELECT username, role, is_active FROM users;
     SELECT title, moderation_status, view_count FROM tips;
     ```
   *Expected Result*: 9 normalized tables; 5 seed users (1 ADMIN, 4 GARDENERS); seed tips displaying approved and pending statuses.
3. **Start Apache Tomcat 10.1**:
   - Deploy `target/gardenconnect.war` to `webapps/` and start Tomcat.
   - Open [http://localhost:8080/gardenconnect/](http://localhost:8080/gardenconnect/).
4. **Demonstrate Gardener Persona**:
   - Log in as `sarah_green` (Password: `Gardener@123`).
   - Navigate to **Personal Dashboard** (`/gardener/dashboard`) $\rightarrow$ verify KPI metrics.
   - Navigate to **Projects** (`/gardener/projects`) $\rightarrow$ adjust progress slider to 100% $\rightarrow$ observe auto-status change to `COMPLETED`.
   - Submit a new gardening tip $\rightarrow$ observe that it appears in personal tips as `PENDING` and is NOT visible on the public feed.
5. **Demonstrate Content Moderation & Administrative Persona**:
   - Log out and log in as `admin` (Password: `Admin@123456`).
   - Navigate to **Moderation Queue** (`/admin/moderation`) $\rightarrow$ locate the tip submitted by Sarah $\rightarrow$ click **Approve & Publish**.
   - Navigate to public tips feed (`/tips`) $\rightarrow$ verify the approved tip is now live for all visitors.
6. **Demonstrate Security & Authorization Filters**:
   - Attempt to access [http://localhost:8080/gardenconnect/admin/dashboard](http://localhost:8080/gardenconnect/admin/dashboard) while logged out $\rightarrow$ observe redirect to `/login`.
   - Log in as `sarah_green` (Gardener) and navigate to `/admin/dashboard` $\rightarrow$ observe custom `403.jsp` (Access Denied).
7. **Inspect Asynchronous Concurrency & Audit Trail**:
   - As `admin`, navigate to `/admin/activity` $\rightarrow$ verify audit entries recorded asynchronously by `GardenConnect-AsyncLogger`.
