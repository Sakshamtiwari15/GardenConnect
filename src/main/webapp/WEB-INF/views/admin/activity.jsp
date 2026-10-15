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
                <span class="badge bg-danger px-3 py-1 rounded-pill fw-bold mb-1">Audit Trail</span>
                <h1 class="h3 fw-bold text-dark mb-0">Platform Activity & Security Logs</h1>
            </div>
            <div class="mt-3 mt-md-0">
                <span class="badge bg-secondary bg-opacity-10 text-dark border px-3 py-2">
                    <i class="bi bi-cpu me-1 text-success"></i> Asynchronously Logged via ExecutorService Worker
                </span>
            </div>
        </div>

        <!-- Filter Toolbar -->
        <div class="card card-botanical p-3 border-0 shadow-sm mb-4">
            <form action="${pageContext.request.contextPath}/admin/activity" method="get" class="row g-2 align-items-center">
                <div class="col-md-5">
                    <select name="action" class="form-select">
                        <option value="">All Actions & Audit Events</option>
                        <option value="USER_REGISTER" ${actionFilter == 'USER_REGISTER' ? 'selected' : ''}>USER_REGISTER (New Member Registrations)</option>
                        <option value="USER_LOGIN" ${actionFilter == 'USER_LOGIN' ? 'selected' : ''}>USER_LOGIN (User Authentications)</option>
                        <option value="USER_ROLE_CHANGE" ${actionFilter == 'USER_ROLE_CHANGE' ? 'selected' : ''}>USER_ROLE_CHANGE (Role Transitions)</option>
                        <option value="USER_STATUS_CHANGE" ${actionFilter == 'USER_STATUS_CHANGE' ? 'selected' : ''}>USER_STATUS_CHANGE (Account Lock/Unlock)</option>
                        <option value="TIP_CREATE" ${actionFilter == 'TIP_CREATE' ? 'selected' : ''}>TIP_CREATE (Gardening Tip Submissions)</option>
                        <option value="TIP_APPROVE" ${actionFilter == 'TIP_APPROVE' ? 'selected' : ''}>TIP_APPROVE (Moderation Approvals)</option>
                        <option value="TIP_REJECT" ${actionFilter == 'TIP_REJECT' ? 'selected' : ''}>TIP_REJECT (Moderation Rejections)</option>
                        <option value="DISCUSSION_CREATE" ${actionFilter == 'DISCUSSION_CREATE' ? 'selected' : ''}>DISCUSSION_CREATE (New Discussions)</option>
                        <option value="COMMENT_CREATE" ${actionFilter == 'COMMENT_CREATE' ? 'selected' : ''}>COMMENT_CREATE (Discussion Replies)</option>
                        <option value="PROJECT_CREATE" ${actionFilter == 'PROJECT_CREATE' ? 'selected' : ''}>PROJECT_CREATE (Gardening Projects)</option>
                        <option value="SETTING_UPDATE" ${actionFilter == 'SETTING_UPDATE' ? 'selected' : ''}>SETTING_UPDATE (System Config Changes)</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <select name="size" class="form-select">
                        <option value="15" ${pageResult.pageSize == 15 ? 'selected' : ''}>15 records per page</option>
                        <option value="30" ${pageResult.pageSize == 30 ? 'selected' : ''}>30 records per page</option>
                        <option value="50" ${pageResult.pageSize == 50 ? 'selected' : ''}>50 records per page</option>
                    </select>
                </div>
                <div class="col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-outline-success flex-grow-1"><i class="bi bi-funnel"></i> Filter Logs</button>
                    <c:if test="${not empty actionFilter}">
                        <a href="${pageContext.request.contextPath}/admin/activity" class="btn btn-outline-secondary" title="Reset Filter"><i class="bi bi-x-circle"></i></a>
                    </c:if>
                </div>
            </form>
        </div>

        <!-- Activity Log Table -->
        <div class="card card-botanical border-0 shadow-sm mb-4">
            <div class="card-header bg-white border-0 py-3 d-flex justify-content-between align-items-center">
                <h6 class="fw-bold text-dark mb-0">
                    <i class="bi bi-journal-text text-danger me-2"></i> Audit Records (${pageResult.totalItems} entries)
                </h6>
                <span class="text-muted small">Page ${pageResult.currentPage} of ${pageResult.totalPages}</span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0 font-monospace small">
                    <thead class="table-light font-sans-serif">
                        <tr>
                            <th scope="col" style="width: 70px;">ID</th>
                            <th scope="col" style="width: 170px;">Timestamp</th>
                            <th scope="col" style="width: 160px;">Action</th>
                            <th scope="col">Actor</th>
                            <th scope="col">Entity</th>
                            <th scope="col">Details & Context</th>
                            <th scope="col">IP Address</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pageResult.items}">
                                <tr>
                                    <td colspan="7" class="text-center py-5 text-muted">
                                        <i class="bi bi-journal-x display-4 d-block mb-3 text-muted"></i>
                                        <p class="mb-0 fw-semibold">No activity logs recorded matching this filter.</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="log" items="${pageResult.items}">
                                    <tr>
                                        <td class="text-muted">#${log.id}</td>
                                        <td class="text-dark">
                                            <fmt:formatDate value="${log.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.action.startsWith('USER_LOGIN')}">
                                                    <span class="badge bg-info text-dark">${log.action}</span>
                                                </c:when>
                                                <c:when test="${log.action.contains('APPROVE')}">
                                                    <span class="badge bg-success text-white">${log.action}</span>
                                                </c:when>
                                                <c:when test="${log.action.contains('REJECT') or log.action.contains('DELETE')}">
                                                    <span class="badge bg-danger text-white">${log.action}</span>
                                                </c:when>
                                                <c:when test="${log.action.contains('CREATE')}">
                                                    <span class="badge bg-primary text-white">${log.action}</span>
                                                </c:when>
                                                <c:when test="${log.action.contains('SETTING')}">
                                                    <span class="badge bg-warning text-dark">${log.action}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary text-white">${log.action}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <strong><c:out value="${not empty log.username ? log.username : 'System'}" /></strong>
                                            <c:if test="${not empty log.userId}">
                                                <span class="text-muted small">(ID: ${log.userId})</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty log.entityType}">
                                                <span class="badge bg-light text-dark border">
                                                    ${log.entityType} <c:if test="${not empty log.entityId}">#${log.entityId}</c:if>
                                                </span>
                                            </c:if>
                                        </td>
                                        <td class="text-wrap" style="max-width: 320px;">
                                            <c:out value="${log.details}" />
                                        </td>
                                        <td class="text-muted">
                                            <c:out value="${not empty log.ipAddress ? log.ipAddress : '-'}" />
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
                    <nav aria-label="Activity Log Pagination">
                        <ul class="pagination justify-content-center mb-0">
                            <li class="page-item ${!pageResult.hasPreviousPage() ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/activity?page=${pageResult.currentPage - 1}&size=${pageResult.pageSize}&action=${actionFilter != null ? actionFilter : ''}">&laquo; Previous</a>
                            </li>
                            <c:forEach begin="1" end="${pageResult.totalPages}" var="p">
                                <li class="page-item ${p == pageResult.currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/admin/activity?page=${p}&size=${pageResult.pageSize}&action=${actionFilter != null ? actionFilter : ''}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${!pageResult.hasNextPage() ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/activity?page=${pageResult.currentPage + 1}&size=${pageResult.pageSize}&action=${actionFilter != null ? actionFilter : ''}">Next &raquo;</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
