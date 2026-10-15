# GardenConnect - Database Schema Specification

## 1. Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    USERS ||--o{ TIPS : "authors"
    USERS ||--o{ DISCUSSIONS : "creates"
    USERS ||--o{ COMMENTS : "writes"
    USERS ||--o{ GARDENING_PROJECTS : "manages"
    USERS ||--o{ ACTIVITY_LOGS : "triggers"
    USERS ||--o{ SYSTEM_SETTINGS : "updates"

    TIP_CATEGORIES ||--o{ TIPS : "categorizes"
    DISCUSSION_CATEGORIES ||--o{ DISCUSSIONS : "classifies"
    DISCUSSIONS ||--o{ COMMENTS : "contains"

    USERS {
        BIGINT id PK
        VARCHAR username UK
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR full_name
        VARCHAR role
        VARCHAR bio
        VARCHAR location
        VARCHAR avatar_url
        BOOLEAN is_active
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    TIP_CATEGORIES {
        INT id PK
        VARCHAR name UK
        VARCHAR slug UK
        VARCHAR description
        VARCHAR icon_class
        INT display_order
    }

    TIPS {
        BIGINT id PK
        BIGINT user_id FK
        INT category_id FK
        VARCHAR title
        VARCHAR summary
        TEXT content
        VARCHAR image_url
        VARCHAR moderation_status
        VARCHAR rejection_reason
        BIGINT reviewed_by FK
        TIMESTAMP reviewed_at
        INT view_count
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    DISCUSSION_CATEGORIES {
        INT id PK
        VARCHAR name UK
        VARCHAR slug UK
        VARCHAR description
        INT display_order
    }

    DISCUSSIONS {
        BIGINT id PK
        BIGINT user_id FK
        INT category_id FK
        VARCHAR title
        TEXT content
        VARCHAR moderation_status
        VARCHAR rejection_reason
        BIGINT reviewed_by FK
        TIMESTAMP reviewed_at
        INT view_count
        INT comment_count
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    COMMENTS {
        BIGINT id PK
        BIGINT discussion_id FK
        BIGINT user_id FK
        TEXT content
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    GARDENING_PROJECTS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR title
        TEXT description
        VARCHAR plant_crop_type
        DATE start_date
        DATE target_completion_date
        VARCHAR status
        INT progress_percentage
        TEXT notes
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    SYSTEM_SETTINGS {
        VARCHAR setting_key PK
        TEXT setting_value
        VARCHAR description
        VARCHAR data_type
        BIGINT updated_by FK
        TIMESTAMP updated_at
    }

    ACTIVITY_LOGS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR username
        VARCHAR action
        VARCHAR entity_type
        BIGINT entity_id
        TEXT details
        VARCHAR ip_address
        TIMESTAMP created_at
    }
```

---

## 2. Table Data Dictionary

### 2.1 Table: `users`
Stores authenticated user accounts for both gardeners and administrators.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | `PK, AUTO_INCREMENT` | Unique identifier for the user account |
| `username` | `VARCHAR(50)` | `NOT NULL, UNIQUE` | Unique login username (alphanumeric + underscore) |
| `email` | `VARCHAR(150)` | `NOT NULL, UNIQUE` | Unique email address for notifications and login |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | Cryptographic BCrypt salted password hash |
| `full_name` | `VARCHAR(100)` | `NOT NULL` | Display name of the community member |
| `role` | `VARCHAR(20)` | `NOT NULL, DEFAULT 'GARDENER'` | RBAC role: `GARDENER` or `ADMIN` |
| `bio` | `TEXT` | `NULL` | Personal gardening background summary |
| `location` | `VARCHAR(100)` | `NULL` | Geographic hardiness zone or city |
| `avatar_url` | `VARCHAR(255)` | `NULL` | Profile avatar image path or URL |
| `is_active` | `BOOLEAN` | `NOT NULL, DEFAULT TRUE` | Active/suspended account flag |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Account creation timestamp |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Last profile update timestamp |

### 2.2 Table: `tip_categories`
Taxonomy for gardening advice and community guides.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | `PK, AUTO_INCREMENT` | Unique category identifier |
| `name` | `VARCHAR(50)` | `NOT NULL, UNIQUE` | Display name (e.g., Vegetables, Pest Control) |
| `slug` | `VARCHAR(60)` | `NOT NULL, UNIQUE` | URL-friendly slug identifier |
| `description` | `VARCHAR(255)` | `NULL` | Brief category summary |
| `icon_class` | `VARCHAR(50)` | `DEFAULT 'bi-flower1'` | Bootstrap Icon CSS class |
| `display_order` | `INT` | `DEFAULT 0` | UI sort ordering |

### 2.3 Table: `tips`
Gardening tips, guides, and practical advice submitted by gardeners.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | `PK, AUTO_INCREMENT` | Unique tip identifier |
| `user_id` | `BIGINT` | `NOT NULL, FK -> users(id)` | Author user ID |
| `category_id` | `INT` | `NOT NULL, FK -> tip_categories(id)` | Associated category |
| `title` | `VARCHAR(150)` | `NOT NULL` | Tip title headline |
| `summary` | `VARCHAR(300)` | `NOT NULL` | Short overview shown in card feeds |
| `content` | `TEXT` | `NOT NULL` | Detailed instructions and steps |
| `image_url` | `VARCHAR(255)` | `NULL` | Optional illustrative photo |
| `moderation_status`| `VARCHAR(20)` | `DEFAULT 'PENDING'` | Moderation state: `PENDING`, `APPROVED`, `REJECTED` |
| `rejection_reason` | `TEXT` | `NULL` | Administrator explanation if rejected |
| `reviewed_by` | `BIGINT` | `NULL, FK -> users(id)` | Administrator ID who moderated the record |
| `reviewed_at` | `TIMESTAMP` | `NULL` | Moderation timestamp |
| `view_count` | `INT` | `DEFAULT 0` | Total views count |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Submission timestamp |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Modification timestamp |

### 2.4 Table: `discussions` & `comments`
Community forum threads and replies.
- `discussions`: Contains topic title, body, category, author foreign key, and moderation state.
- `comments`: Linked via `discussion_id` with `ON DELETE CASCADE`. Replies are deleted if the parent discussion is removed.

### 2.5 Table: `gardening_projects`
Personal tracking for crop cycles, garden plots, and seasonal projects.
- `status`: `PLANNED`, `IN_PROGRESS`, `COMPLETED`, `ON_HOLD`.
- `progress_percentage`: Integer `CHECK (progress_percentage BETWEEN 0 AND 100)`.
- Enforces strict user isolation: a gardener can only view and mutate their own projects.

### 2.6 Table: `system_settings`
Runtime platform parameters stored in MySQL.
- Key-value configuration: `community_name`, `maintenance_mode`, `registration_open`, `max_upload_mb`, `default_page_size`, `moderation_policy`.

### 2.7 Table: `activity_logs`
Asynchronously populated audit trail.
- Stores actor identity, action type, target entity, timestamp, and IP address.

---

## 3. Referential Integrity & Cascade Rules

1. **User Deletion**:
   - `tips.user_id`: `ON DELETE RESTRICT` or `CASCADE` depending on administrative policy.
   - `gardening_projects.user_id`: `ON DELETE CASCADE` (personal projects belong solely to the user).
   - `activity_logs.user_id`: `ON DELETE SET NULL` (preserves audit records even if user account is removed).
2. **Discussion Cascade**:
   - `comments.discussion_id`: `ON DELETE CASCADE` ensures no orphaned comments remain when a discussion is deleted.

---

## 4. Database Setup & Initialization

```bash
# 1. Access MySQL Server
mysql -u root -p

# 2. Execute schema initialization
source c:/shaurya/Online Gardening Community Platform/database/schema.sql;

# 3. Seed initial categories, settings, and demo accounts
source c:/shaurya/Online Gardening Community Platform/database/seed.sql;
```
