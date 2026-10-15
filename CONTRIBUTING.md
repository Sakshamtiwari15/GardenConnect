# Contributing to GardenConnect

Thank you for your interest in contributing to GardenConnect! We welcome community contributions, bug reports, and feature suggestions.

## Code of Conduct
- Be respectful, constructive, and inclusive.
- Focus on clean, maintainable, and well-tested Java code.

## Development Workflow
1. Fork the repository and clone your fork locally:
   ```bash
   git clone https://github.com/Sakshamtiwari15/GardenConnect.git
   cd GardenConnect
   ```
2. Create a new topic branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Set up the MySQL database using `database/schema.sql` and `database/seed.sql`.
4. Configure database credentials in your environment or via `db.properties`.
5. Verify that existing tests pass:
   ```bash
   mvn clean test
   ```
6. Make your code changes adhering to our architectural patterns:
   - Use Jakarta Servlet API (`jakarta.servlet.*`).
   - Maintain pure MVC separation: Controller -> Service -> DAO -> MySQL.
   - Never write business logic or database queries inside JSP views.
   - Use `PreparedStatement` for all SQL queries with parameters.
7. Add JUnit 5 unit tests for any new business logic, validation rules, or services.
8. Commit your changes with clear, descriptive commit messages:
   ```bash
   git commit -m "feat(tips): add organic gardening filter option"
   ```
9. Push to your branch and open a Pull Request.

## Coding Standards
- Follow standard Java code conventions (CamelCase for classes, camelCase for methods/variables).
- Always use try-with-resources when handling JDBC connections, statements, and result sets.
- Ensure all public-facing methods in services and DAOs are documented.
- Protect all state-changing endpoints against CSRF and validate user permissions.
