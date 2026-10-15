# Changelog

All notable changes to the GardenConnect project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-10-15
### Added
- Complete MVC Architecture using Java 17, Jakarta Servlets 6.0, and JSP/JSTL on Apache Tomcat 10.1.
- Database layer utilizing MySQL 8.0, normalized schema, and HikariCP connection pooling.
- BCrypt password hashing and session-based authentication with role-based authorization (`ADMIN` and `GARDENER`).
- CSRF token protection for all state-changing forms and output sanitization for XSS mitigation.
- Community Gardening Tips module: full CRUD, categorization, pagination, search, and moderation status.
- Community Discussions module: discussion threads, category filtering, search, and nested user comments.
- Personal Gardening Projects manager: project tracking, progress bar calculations (0-100%), status lifecycles (`PLANNED`, `IN_PROGRESS`, `COMPLETED`, `ON_HOLD`).
- Administrator Portal: live metrics overview, user management (role promotion, deactivation), content moderation queue (approve/reject), system settings, and audit logs.
- Asynchronous activity logging powered by Java `ExecutorService`, thread-safe tracking (`AtomicInteger`, `ConcurrentHashMap`), and graceful shutdown in `AppContextListener`.
- Custom botanical design system styled with HTML5, CSS3, Bootstrap 5, and Chart.js.
- JUnit 5 test suite covering validation, security, business logic, pagination, and concurrency.
- Comprehensive documentation: Architecture, Database Schema, API/Routes, Academic Rubric Mapping, and Installation Guide.
- Integration audit and verification: verified all 14 servlet routes, 29 JSP views, 9 database tables, 5-stage filter chain in web.xml, and 43 automated unit tests.
