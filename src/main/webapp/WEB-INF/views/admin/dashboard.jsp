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
        <!-- Top Bar Header -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
            <div>
                <span class="badge bg-danger px-3 py-1 rounded-pill fw-bold mb-1">Administrative Overview</span>
                <h1 class="h3 fw-bold text-dark mb-0">Platform Health & Analytics</h1>
            </div>
            <div class="mt-3 mt-md-0 d-flex gap-2">
                <a href="${pageContext.request.contextPath}/admin/moderation" class="btn btn-warning text-dark position-relative">
                    <i class="bi bi-shield-check me-1"></i> Moderation Queue
                    <c:if test="${stats.pendingModerationCount > 0}">
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                            ${stats.pendingModerationCount}
                        </span>
                    </c:if>
                </a>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-botanical">
                    <i class="bi bi-people me-1"></i> Manage Users
                </a>
            </div>
        </div>

        <!-- Moderation Pending Alert Banner -->
        <c:if test="${stats.pendingModerationCount > 0}">
            <div class="alert alert-warning border-0 shadow-sm d-flex justify-content-between align-items-center mb-4" role="alert">
                <div class="d-flex align-items-center gap-2">
                    <i class="bi bi-exclamation-triangle-fill fs-4 text-warning"></i>
                    <div>
                        <strong>Action Needed:</strong> There are <strong>${stats.pendingModerationCount}</strong> gardening tips currently waiting for your review.
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/admin/moderation" class="btn btn-sm btn-dark">Review Queue</a>
            </div>
        </c:if>

        <!-- KPI Statistic Cards (Real MySQL Database Data) -->
        <div class="row g-3 mb-4">
            <!-- Total Users -->
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Total Users</span>
                            <h3 class="fw-bold mb-0 text-dark mt-1">${stats.totalUsers}</h3>
                            <small class="text-muted">${stats.totalGardeners} Gardeners &bull; ${stats.totalAdmins} Admins</small>
                        </div>
                        <div class="bg-primary bg-opacity-10 text-primary p-3 rounded-circle">
                            <i class="bi bi-people-fill fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Gardening Tips -->
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Gardening Tips</span>
                            <h3 class="fw-bold mb-0 text-success mt-1">${stats.totalTips}</h3>
                            <small class="text-muted">${stats.approvedTipsCount} Approved &bull; ${stats.pendingModerationCount} Pending</small>
                        </div>
                        <div class="bg-success bg-opacity-10 text-success p-3 rounded-circle">
                            <i class="bi bi-journal-check fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Discussions & Replies -->
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Discussions</span>
                            <h3 class="fw-bold mb-0 text-dark mt-1">${stats.totalDiscussions}</h3>
                            <small class="text-muted">${stats.totalComments} total comments posted</small>
                        </div>
                        <div class="bg-warning bg-opacity-10 text-warning p-3 rounded-circle">
                            <i class="bi bi-chat-quote-fill fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Gardening Projects -->
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Projects Tracked</span>
                            <h3 class="fw-bold mb-0 text-primary mt-1">${stats.totalProjects}</h3>
                            <small class="text-muted">Across all community members</small>
                        </div>
                        <div class="bg-info bg-opacity-10 text-info p-3 rounded-circle">
                            <i class="bi bi-kanban-fill fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Chart.js Analytical Visualizations -->
        <div class="row g-4 mb-4">
            <!-- Tips by Category Chart -->
            <div class="col-lg-6">
                <div class="card card-botanical p-4 border-0 shadow-sm h-100">
                    <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                        <i class="bi bi-pie-chart text-success"></i> Tips Distribution by Category
                    </h5>
                    <div style="height: 280px; position: relative;">
                        <canvas id="tipsByCategoryChart"></canvas>
                    </div>
                </div>
            </div>

            <!-- Projects by Status Chart -->
            <div class="col-lg-6">
                <div class="card card-botanical p-4 border-0 shadow-sm h-100">
                    <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                        <i class="bi bi-bar-chart text-success"></i> Personal Projects by Status
                    </h5>
                    <div style="height: 280px; position: relative;">
                        <canvas id="projectsByStatusChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Audit Activity Trail -->
        <div class="card card-botanical p-4 border-0 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
                    <i class="bi bi-activity text-danger"></i> Recent Platform Activity
                </h5>
                <a href="${pageContext.request.contextPath}/admin/activity" class="btn btn-sm btn-outline-secondary">
                    View Full Audit Trail
                </a>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col">Timestamp</th>
                            <th scope="col">User</th>
                            <th scope="col">Action</th>
                            <th scope="col">Details</th>
                            <th scope="col">IP Address</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="log" items="${recentLogs}">
                            <tr>
                                <td class="small text-muted">
                                    <fmt:formatDate value="${log.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                </td>
                                <td>
                                    <strong><c:out value="${log.username != null ? log.username : 'SYSTEM'}" /></strong>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        <c:out value="${log.action}" />
                                    </span>
                                </td>
                                <td class="small text-muted">
                                    <c:out value="${log.details}" />
                                </td>
                                <td class="small font-monospace">
                                    <c:out value="${log.ipAddress}" />
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Prepare Data for Chart.js Script -->
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const catLabels = [
            <c:forEach var="entry" items="${stats.tipsByCategory}" varStatus="loop">
                "<c:out value='${entry.key}' />"${!loop.last ? ',' : ''}
            </c:forEach>
        ];
        const catData = [
            <c:forEach var="entry" items="${stats.tipsByCategory}" varStatus="loop">
                ${entry.value}${!loop.last ? ',' : ''}
            </c:forEach>
        ];

        const statusLabels = [
            <c:forEach var="entry" items="${stats.projectsByStatus}" varStatus="loop">
                "<c:out value='${entry.key}' />"${!loop.last ? ',' : ''}
            </c:forEach>
        ];
        const statusData = [
            <c:forEach var="entry" items="${stats.projectsByStatus}" varStatus="loop">
                ${entry.value}${!loop.last ? ',' : ''}
            </c:forEach>
        ];

        initAdminCharts(catLabels, catData, statusLabels, statusData);
    });
</script>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
