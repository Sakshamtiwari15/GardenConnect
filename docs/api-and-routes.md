# GardenConnect - API and Routes Directory

All HTTP endpoints are mapped using Jakarta Servlet `@WebServlet` annotations. State-changing requests (`POST`) require valid CSRF tokens (`_csrf` parameter or header) and appropriate user session roles.

---

## 1. Public & Guest Routes

| HTTP Method | Route | Controller Class | Description | Required Role | CSRF Protected |
|---|---|---|---|---|---|
| `GET` | `/` | `HomeController` | Community landing page with metrics and featured tips | Public | No |
| `GET` | `/tips` | `TipController` | Browse approved gardening tips with search & category filter | Public | No |
| `GET` | `/tips/view` | `TipController` | View single approved gardening tip detail & full instructions | Public | No |
| `GET` | `/discussions` | `DiscussionController` | Browse approved community discussions | Public | No |
| `GET` | `/discussions/view`| `DiscussionController` | View discussion thread, comments, and reply form | Public | No |

---

## 2. Authentication Routes

| HTTP Method | Route | Controller Class | Description | Required Role | CSRF Protected |
|---|---|---|---|---|---|
| `GET` | `/login` | `AuthController` | Display user sign-in form | Public / Guest | No |
| `POST` | `/login` | `AuthController` | Authenticate username/password & create session | Public / Guest | Yes (`_csrf`) |
| `GET` | `/register` | `AuthController` | Display gardener account registration form | Public / Guest | No |
| `POST` | `/register` | `AuthController` | Validate input & create new gardener account | Public / Guest | Yes (`_csrf`) |
| `POST` | `/logout` | `AuthController` | Invalidate HTTP session and redirect to home | Authenticated | Yes (`_csrf`) |

---

## 3. Gardener Portal Routes (`/gardener/*`)

*All routes in this group require an active session with role `GARDENER` or `ADMIN`.*

| HTTP Method | Route | Controller Class | Description | CSRF Protected |
|---|---|---|---|---|
| `GET` | `/gardener/dashboard` | `GardenerDashboardController` | Personal dashboard with stats, recent tips, and projects | No |
| `GET` | `/gardener/profile` | `GardenerProfileController` | View and edit personal profile and password | No |
| `POST` | `/gardener/profile` | `GardenerProfileController` | Update bio, location, full name, or password | Yes (`_csrf`) |
| `GET` | `/gardener/tips` | `GardenerTipController` | Manage personal tips and moderation states | No |
| `GET` | `/gardener/tips/create` | `GardenerTipController` | Display new tip creation form | No |
| `POST` | `/gardener/tips/create` | `GardenerTipController` | Submit new tip into moderation queue | Yes (`_csrf`) |
| `GET` | `/gardener/tips/edit` | `GardenerTipController` | Display tip editing form (author only) | No |
| `POST` | `/gardener/tips/edit` | `GardenerTipController` | Update owned tip | Yes (`_csrf`) |
| `POST` | `/gardener/tips/delete` | `GardenerTipController` | Delete owned tip | Yes (`_csrf`) |
| `GET` | `/gardener/discussions` | `GardenerDiscussionController`| View discussions started by current user | No |
| `GET` | `/gardener/discussions/create`| `GardenerDiscussionController`| Display new discussion form | No |
| `POST` | `/gardener/discussions/create`| `GardenerDiscussionController`| Publish new community topic | Yes (`_csrf`) |
| `POST` | `/gardener/comments/create`| `DiscussionController` | Add a comment to an active discussion | Yes (`_csrf`) |
| `POST` | `/gardener/comments/delete`| `DiscussionController` | Delete user's own comment | Yes (`_csrf`) |
| `GET` | `/gardener/projects` | `GardenerProjectController` | View and filter personal gardening projects | No |
| `GET` | `/gardener/projects/create`| `GardenerProjectController` | Display new project creation form | No |
| `POST` | `/gardener/projects/create`| `GardenerProjectController` | Create a gardening project tracker | Yes (`_csrf`) |
| `GET` | `/gardener/projects/edit` | `GardenerProjectController` | Display project edit form (owner only) | No |
| `POST` | `/gardener/projects/edit` | `GardenerProjectController` | Update project fields and notes | Yes (`_csrf`) |
| `POST` | `/gardener/projects/progress`| `GardenerProjectController` | Update progress slider (0-100%) and status | Yes (`_csrf`) |
| `POST` | `/gardener/projects/delete` | `GardenerProjectController` | Remove a personal project | Yes (`_csrf`) |

---

## 4. Administrator Portal Routes (`/admin/*`)

*All routes in this group strictly require an active session with role `ADMIN`.*

| HTTP Method | Route | Controller Class | Description | CSRF Protected |
|---|---|---|---|---|
| `GET` | `/admin/dashboard` | `AdminDashboardController` | Platform KPI stats, system health, and Chart.js | No |
| `GET` | `/admin/users` | `AdminUserController` | Directory of users with search, role filter, pagination | No |
| `POST` | `/admin/users/create` | `AdminUserController` | Provision a new user account with assigned role | Yes (`_csrf`) |
| `POST` | `/admin/users/role` | `AdminUserController` | Promote or change a user's role | Yes (`_csrf`) |
| `POST` | `/admin/users/status` | `AdminUserController` | Activate or deactivate a user account | Yes (`_csrf`) |
| `POST` | `/admin/users/delete` | `AdminUserController` | Permanently remove a user account | Yes (`_csrf`) |
| `GET` | `/admin/moderation` | `AdminModerationController` | Moderation queue for pending community content | No |
| `POST` | `/admin/moderation/approve` | `AdminModerationController` | Approve content to publish to public feed | Yes (`_csrf`) |
| `POST` | `/admin/moderation/reject` | `AdminModerationController` | Reject content with documented reason | Yes (`_csrf`) |
| `GET` | `/admin/settings` | `AdminSettingsController` | Manage platform config stored in MySQL | No |
| `POST` | `/admin/settings` | `AdminSettingsController` | Persist updated platform settings & limits | Yes (`_csrf`) |
| `GET` | `/admin/activity` | `AdminActivityController` | Audit trail of security, auth, and data events | No |
