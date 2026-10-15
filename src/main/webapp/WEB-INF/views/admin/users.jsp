<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="dashboard-layout">
    <!-- Admin Sidebar -->
    <jsp:include page="/WEB-INF/views/fragments/admin-sidebar.jsp" />

    <!-- Admin Main Content -->
    <main class="dashboard-content">
        <!-- Top Title Bar -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
            <div>
                <span class="badge bg-danger px-3 py-1 rounded-pill fw-bold mb-1">User Administration</span>
                <h1 class="h3 fw-bold text-dark mb-0">Platform Users Directory</h1>
            </div>
            <div class="mt-3 mt-md-0">
                <button type="button" class="btn btn-botanical shadow-sm" data-bs-toggle="modal" data-bs-target="#createUserModal">
                    <i class="bi bi-person-plus-fill me-1"></i> Provision New User
                </button>
            </div>
        </div>

        <!-- Filter & Search Toolbar -->
        <div class="card card-botanical p-3 border-0 shadow-sm mb-4">
            <form action="${pageContext.request.contextPath}/admin/users" method="get" class="row g-2 align-items-center">
                <div class="col-md-5">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                        <input type="text" name="q" class="form-control border-start-0 ps-0" placeholder="Search by username, full name, or email..." value="<c:out value='${searchQuery}' />" />
                    </div>
                </div>
                <div class="col-md-3">
                    <select name="role" class="form-select">
                        <option value="">All Roles (Gardeners & Admins)</option>
                        <c:forEach var="r" items="${roles}">
                            <option value="${r.name()}" ${selectedRole == r ? 'selected' : ''}>${r.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <select name="size" class="form-select">
                        <option value="10" ${pageResult.pageSize == 10 ? 'selected' : ''}>10 per page</option>
                        <option value="25" ${pageResult.pageSize == 25 ? 'selected' : ''}>25 per page</option>
                        <option value="50" ${pageResult.pageSize == 50 ? 'selected' : ''}>50 per page</option>
                    </select>
                </div>
                <div class="col-md-2 d-flex gap-2">
                    <button type="submit" class="btn btn-outline-success flex-grow-1"><i class="bi bi-funnel"></i> Filter</button>
                    <c:if test="${not empty searchQuery or not empty selectedRole}">
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary" title="Reset Filters"><i class="bi bi-x-circle"></i></a>
                    </c:if>
                </div>
            </form>
        </div>

        <!-- Users Table Card -->
        <div class="card card-botanical border-0 shadow-sm mb-4">
            <div class="card-header bg-white border-0 py-3 d-flex justify-content-between align-items-center">
                <h6 class="fw-bold text-dark mb-0">
                    <i class="bi bi-people-fill text-success me-2"></i> Registered Accounts (${pageResult.totalItems} total)
                </h6>
                <span class="text-muted small">Page ${pageResult.currentPage} of ${pageResult.totalPages}</span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="width: 50px;">ID</th>
                            <th scope="col">User</th>
                            <th scope="col">Contact</th>
                            <th scope="col">Role</th>
                            <th scope="col">Status</th>
                            <th scope="col">Joined</th>
                            <th scope="col" class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="7" class="text-center py-5 text-muted">
                                        <i class="bi bi-people display-4 d-block mb-3 text-muted"></i>
                                        <p class="mb-0 fw-semibold">No user records matched your criteria.</p>
                                        <small>Try clearing the search query or role filter.</small>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="userItem" items="${pageResult.items}">
                                    <tr>
                                        <td class="text-muted small">#${userItem.id}</td>
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="avatar-circle-sm bg-success text-white fw-bold rounded-circle d-flex align-items-center justify-content-center" style="width: 38px; height: 38px; font-size: 0.9rem;">
                                                    ${userItem.username.substring(0, 1).toUpperCase()}
                                                </div>
                                                <div>
                                                    <div class="fw-bold text-dark"><c:out value="${userItem.username}" /></div>
                                                    <small class="text-muted"><c:out value="${userItem.fullName}" /></small>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="small text-dark"><c:out value="${userItem.email}" /></div>
                                            <c:if test="${not empty userItem.location}">
                                                <small class="text-muted"><i class="bi bi-geo-alt"></i> <c:out value="${userItem.location}" /></small>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${userItem.admin}">
                                                    <span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 px-2 py-1">
                                                        <i class="bi bi-shield-shaded me-1"></i> Admin
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 px-2 py-1">
                                                        <i class="bi bi-flower1 me-1"></i> Gardener
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${userItem.active}">
                                                    <span class="badge bg-success text-white rounded-pill px-2">Active</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary text-white rounded-pill px-2">Deactivated</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-muted small">
                                            <c:out value="${userItem.createdAtFormatted}" />
                                        </td>
                                        <td class="text-end">
                                            <div class="btn-group btn-group-sm">
                                                <!-- Role Toggle Dropdown -->
                                                <button type="button" class="btn btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false" title="Change Role">
                                                    <i class="bi bi-person-gear"></i>
                                                </button>
                                                <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                                                    <li><h6 class="dropdown-header">Assign Role</h6></li>
                                                    <c:forEach var="r" items="${roles}">
                                                        <li>
                                                            <form action="${pageContext.request.contextPath}/admin/users/role" method="post" class="dropdown-item p-0">
                                                                <input type="hidden" name="_csrf" value="${csrfToken}" />
                                                                <input type="hidden" name="userId" value="${userItem.id}" />
                                                                <input type="hidden" name="role" value="${r.name()}" />
                                                                <button type="submit" class="btn btn-link dropdown-item py-1 ${userItem.role == r ? 'fw-bold text-success' : ''}">
                                                                    <c:if test="${userItem.role == r}"><i class="bi bi-check2 me-1"></i></c:if>
                                                                    ${r.displayName}
                                                                </button>
                                                            </form>
                                                        </li>
                                                    </c:forEach>
                                                </ul>

                                                <!-- Activate / Deactivate Button -->
                                                <c:choose>
                                                    <c:when test="${userItem.active}">
                                                        <form action="${pageContext.request.contextPath}/admin/users/status" method="post" class="d-inline" onsubmit="return confirm('Are you sure you want to deactivate ${userItem.username}? They will not be able to log in.');">
                                                            <input type="hidden" name="_csrf" value="${csrfToken}" />
                                                            <input type="hidden" name="userId" value="${userItem.id}" />
                                                            <input type="hidden" name="active" value="false" />
                                                            <button type="submit" class="btn btn-outline-warning" title="Deactivate Account">
                                                                <i class="bi bi-person-x"></i>
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form action="${pageContext.request.contextPath}/admin/users/status" method="post" class="d-inline">
                                                            <input type="hidden" name="_csrf" value="${csrfToken}" />
                                                            <input type="hidden" name="userId" value="${userItem.id}" />
                                                            <input type="hidden" name="active" value="true" />
                                                            <button type="submit" class="btn btn-outline-success" title="Activate Account">
                                                                <i class="bi bi-person-check"></i>
                                                            </button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>

                                                <!-- Delete Button -->
                                                <c:if test="${userItem.id != sessionScope.currentUser.id}">
                                                    <form action="${pageContext.request.contextPath}/admin/users/delete" method="post" class="d-inline" onsubmit="return confirm('WARNING: Permanently delete ${userItem.username}? This action is irreversible.');">
                                                        <input type="hidden" name="_csrf" value="${csrfToken}" />
                                                        <input type="hidden" name="userId" value="${userItem.id}" />
                                                        <button type="submit" class="btn btn-outline-danger" title="Permanently Delete User">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <!-- Pagination Bar -->
            <c:if test="${pageResult.totalPages > 1}">
                <div class="card-footer bg-white border-0 py-3">
                    <nav aria-label="Users Page Navigation">
                        <ul class="pagination justify-content-center mb-0">
                            <li class="page-item ${!pageResult.hasPreviousPage() ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${pageResult.currentPage - 1}&size=${pageResult.pageSize}&role=${selectedRole != null ? selectedRole.name() : ''}&q=${searchQuery != null ? searchQuery : ''}">&laquo; Previous</a>
                            </li>
                            <c:forEach begin="1" end="${pageResult.totalPages}" var="p">
                                <li class="page-item ${p == pageResult.currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${p}&size=${pageResult.pageSize}&role=${selectedRole != null ? selectedRole.name() : ''}&q=${searchQuery != null ? searchQuery : ''}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${!pageResult.hasNextPage() ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${pageResult.currentPage + 1}&size=${pageResult.pageSize}&role=${selectedRole != null ? selectedRole.name() : ''}&q=${searchQuery != null ? searchQuery : ''}">Next &raquo;</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </main>
</div>

<!-- Modal: Create New User -->
<div class="modal fade" id="createUserModal" tabindex="-1" aria-labelledby="createUserModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="${pageContext.request.contextPath}/admin/users/create" method="post">
                <input type="hidden" name="_csrf" value="${csrfToken}" />
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title fw-bold" id="createUserModalLabel"><i class="bi bi-person-plus-fill me-2"></i> Provision Platform Account</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="newUsername" class="form-label fw-semibold">Username <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="newUsername" name="username" required pattern="[A-Za-z0-9_]{3,30}" placeholder="e.g. botanical_master" />
                        <small class="text-muted">3-30 letters, numbers, or underscores.</small>
                    </div>
                    <div class="mb-3">
                        <label for="newEmail" class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" id="newEmail" name="email" required placeholder="user@gardenconnect.org" />
                    </div>
                    <div class="mb-3">
                        <label for="newFullName" class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="newFullName" name="fullName" required placeholder="e.g. Dr. Jane Watson" />
                    </div>
                    <div class="mb-3">
                        <label for="newPassword" class="form-label fw-semibold">Temporary Password <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" id="newPassword" name="password" required minlength="8" placeholder="At least 8 characters" />
                    </div>
                    <div class="mb-3">
                        <label for="newRole" class="form-label fw-semibold">Role <span class="text-danger">*</span></label>
                        <select class="form-select" id="newRole" name="role" required>
                            <c:forEach var="r" items="${roles}">
                                <option value="${r.name()}">${r.displayName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-botanical"><i class="bi bi-check-circle me-1"></i> Create User</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
