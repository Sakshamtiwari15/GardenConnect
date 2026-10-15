# GardenConnect Database Documentation

## Database Overview
GardenConnect uses MySQL 8.0+ with the `InnoDB` storage engine and `utf8mb4` character encoding. The database name is `gardenconnect`.

## Setup Instructions

### 1. Prerequisite
Ensure MySQL Server 8.0+ is installed and running on your host machine.

### 2. Creating Database & Schema
Run the initialization script using the MySQL command-line client:
```bash
mysql -u root -p < database/schema.sql
```
Or execute through MySQL Workbench, DBeaver, or another SQL GUI tool.

### 3. Loading Seed Data
Populate default categories, administrative settings, sample gardeners, tips, discussions, and projects:
```bash
mysql -u root -p gardenconnect < database/seed.sql
```

## Default Accounts Seeded
| Username | Role | Password | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `ADMIN` | `Admin@123456` | Platform Administrator with full access to moderation and user management |
| `sarah_green` | `GARDENER` | `Gardener@123` | Master Gardener account with sample tips and projects |
| `marcus_urban` | `GARDENER` | `Gardener@123` | Balcony gardener with active projects and discussions |
| `elena_botanist` | `GARDENER` | `Gardener@123` | Soil scientist with sample tips and comments |
| `david_veggies` | `GARDENER` | `Gardener@123` | Homestead gardener with sample projects |

## Schema Design Highlights
- **Referential Integrity**: Cascading deletes for user-owned content (`ON DELETE CASCADE`), restricted deletions for taxonomy categories (`ON DELETE RESTRICT`), and audit log preservation (`ON DELETE SET NULL`).
- **Performance Indexes**: Composite indexes on `(moderation_status, created_at)` for instant community feed queries; index on `(user_id, status)` for fast project dashboard filtering.
- **Data Protection**: Zero plaintext passwords stored (BCrypt salted hash with cost factor 10+).
- **Constraints**: Enforced range check `progress_percentage >= 0 AND progress_percentage <= 100` on gardening projects.
