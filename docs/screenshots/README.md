# GardenConnect - Application Screenshots & Visual Walkthrough

This directory contains visual captures of GardenConnect running locally on **Apache Tomcat 10.1** and **MySQL 8.0**.

---

## Screenshot Inventory & Placeholders

### 1. Public Community Landing Page (`01_home_landing.png`)
- **Route**: `GET /`
- **Features Displayed**:
  - Hero banner with botanical aesthetic and inspiring community slogan
  - Live community metrics counter (Total Gardeners, Active Projects, Approved Tips, Discussions)
  - Categories grid (Vegetables, Herbs, Flowers, Composting, Pest Control, Organic Gardening)
  - Featured gardening tips preview cards
  - Recent community discussion topics preview
  - Responsive footer with navigation and project details

---

### 2. User Authentication & Registration (`02_login_register.png`)
- **Route**: `GET /login` and `GET /register`
- **Features Displayed**:
  - Secure login form with validation alerts
  - Member registration form with password strength guidance
  - CSRF token hidden protection
  - Clean botanical green card styling

---

### 3. Gardener Personal Dashboard (`03_gardener_dashboard.png`)
- **Route**: `GET /gardener/dashboard`
- **Features Displayed**:
  - Personalized welcome banner for authenticated gardener
  - Real database KPI summary cards (My Tips, Active Projects, Discussion Posts)
  - Active personal gardening projects list with interactive percentage progress bars
  - Recent community discussions feed
  - Quick action shortcuts (Start Project, Share Tip, Join Discussion)

---

### 4. Gardening Project Tracker (`04_gardening_projects.png`)
- **Route**: `GET /gardener/projects` and `GET /gardener/projects/create`
- **Features Displayed**:
  - Project cards displaying plant/crop type, start date, target completion date
  - Visual status badges (`PLANNED`, `IN_PROGRESS`, `COMPLETED`, `ON_HOLD`)
  - Color-coded progress bars with inline progress update slider
  - Status filters and project statistics overview

---

### 5. Community Tips Feed & Tip Detail (`05_tips_feed_detail.png`)
- **Route**: `GET /tips` and `GET /tips/view?id=...`
- **Features Displayed**:
  - Search bar and category taxonomy filter
  - Rich tip cards with author avatar, publication date, view counter, and summary
  - Detailed tip reader view with full instructions, formatted steps, and category badges

---

### 6. Administrator Dashboard & Analytics (`06_admin_dashboard.png`)
- **Route**: `GET /admin/dashboard`
- **Features Displayed**:
  - Administrative header with pending moderation alert badge
  - Database-driven KPI metric cards (Total Users, Gardeners, Admins, Tips, Discussions, Projects)
  - **Chart.js Visualizations**:
    - Pie chart: Platform user distribution by role
    - Bar chart: Gardening tips distribution by horticultural category
  - Recent user registrations table
  - Recent system audit activity feed

---

### 7. Content Moderation Review Queue (`07_admin_moderation.png`)
- **Route**: `GET /admin/moderation`
- **Features Displayed**:
  - Filter tabs: Pending Review, Approved, Rejected
  - Pending tip card with author info, category, and content preview
  - One-click "Approve & Publish" action
  - "Reject" modal with required explanation textarea for community feedback

---

### 8. User Management Directory (`08_admin_users.png`)
- **Route**: `GET /admin/users`
- **Features Displayed**:
  - User accounts table with search, role filter, and pagination
  - Role promotion dropdown (Gardener <-> Admin)
  - Account status toggle (Active / Deactivated)
  - Irreversible account deletion with confirmation modal
  - Provision new user account modal

---

### 9. System Settings & Runtime Policies (`09_admin_settings.png`)
- **Route**: `GET /admin/settings`
- **Features Displayed**:
  - Database-backed configuration form
  - Platform identity settings (Community Name, Tagline, Contact Email)
  - Security policies (Maintenance Mode toggle, Registration Open/Closed, Moderation Policy)
  - Operational limits (Max Upload Size MB, Default Pagination Page Size)

---

### 10. Audit Activity Trail (`10_admin_activity.png`)
- **Route**: `GET /admin/activity`
- **Features Displayed**:
  - Asynchronously logged audit records table
  - Filter by event type (`USER_LOGIN`, `TIP_CREATE`, `TIP_APPROVE`, `PROJECT_CREATE`, etc.)
  - Timestamp, actor username, entity ID, context details, and client IP address
  - Pagination controls

---

## How to Add Real Screenshots

1. Start Apache Tomcat 10.1 and deploy `target/gardenconnect.war`.
2. Open your web browser at `http://localhost:8080/gardenconnect/`.
3. Capture screenshots of each route listed above.
4. Save the image files in PNG format in this directory (`docs/screenshots/`) matching the file names listed above.
