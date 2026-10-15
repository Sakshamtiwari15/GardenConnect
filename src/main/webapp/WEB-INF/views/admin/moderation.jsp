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
                <span class="badge bg-danger px-3 py-1 rounded-pill fw-bold mb-1">Content Moderation</span>
                <h1 class="h3 fw-bold text-dark mb-0">Community Review Queue</h1>
            </div>
            <div class="mt-3 mt-md-0 d-flex gap-2">
                <div class="badge bg-warning text-dark p-2 px-3 rounded-pill d-flex align-items-center gap-2 shadow-sm">
                    <i class="bi bi-clock-history fs-6"></i>
                    <span><strong>${pendingTipsCount}</strong> Tips Awaiting Approval</span>
                </div>
            </div>
        </div>

        <!-- Filter & Search Toolbar -->
        <div class="card card-botanical p-3 border-0 shadow-sm mb-4">
            <form action="${pageContext.request.contextPath}/admin/moderation" method="get" class="row g-2 align-items-center">
                <div class="col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                        <input type="text" name="q" class="form-control border-start-0 ps-0" placeholder="Search pending titles or authors..." value="<c:out value='${searchQuery}' />" />
                    </div>
                </div>
                <div class="col-md-4">
                    <select name="status" class="form-select" onchange="this.form.submit()">
                        <c:forEach var="st" items="${statuses}">
                            <option value="${st.name()}" ${selectedStatus == st ? 'selected' : ''}>Status: ${st.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2 d-flex gap-2">
                    <button type="submit" class="btn btn-outline-success flex-grow-1"><i class="bi bi-funnel"></i> Filter</button>
                    <c:if test="${not empty searchQuery or selectedStatus.name() != 'PENDING'}">
                        <a href="${pageContext.request.contextPath}/admin/moderation" class="btn btn-outline-secondary" title="Reset Filters"><i class="bi bi-x-circle"></i></a>
                    </c:if>
                </div>
            </form>
        </div>

        <!-- Status Filter Nav Tabs -->
        <ul class="nav nav-pills mb-4">
            <li class="nav-item">
                <a class="nav-link ${selectedStatus.name() == 'PENDING' ? 'active bg-warning text-dark fw-bold' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/moderation?status=PENDING">
                    <i class="bi bi-hourglass-split me-1"></i> Pending Review (${pendingTipsCount})
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${selectedStatus.name() == 'APPROVED' ? 'active bg-success text-white fw-bold' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/moderation?status=APPROVED">
                    <i class="bi bi-check2-circle me-1"></i> Approved
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${selectedStatus.name() == 'REJECTED' ? 'active bg-danger text-white fw-bold' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/moderation?status=REJECTED">
                    <i class="bi bi-x-circle me-1"></i> Rejected
                </a>
            </li>
        </ul>

        <!-- Moderation Items Queue -->
        <c:choose>
            <c:when test="${empty tipsPage.items}">
                <div class="card card-botanical p-5 border-0 shadow-sm text-center">
                    <i class="bi bi-check2-all display-1 text-success mb-3"></i>
                    <h4 class="fw-bold text-dark">Queue is All Caught Up!</h4>
                    <p class="text-muted mb-0">No gardening submissions found matching <strong>${selectedStatus.displayName}</strong> status.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="d-flex flex-column gap-3 mb-4">
                    <c:forEach var="tipItem" items="${tipsPage.items}">
                        <div class="card card-botanical border-0 shadow-sm p-4">
                            <div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-2">
                                <div>
                                    <span class="badge bg-secondary bg-opacity-10 text-dark border px-2 py-1 mb-1">
                                        <i class="bi bi-tag me-1"></i> <c:out value="${tipItem.categoryName}" />
                                    </span>
                                    <h4 class="h5 fw-bold text-dark mb-1"><c:out value="${tipItem.title}" /></h4>
                                    <div class="small text-muted">
                                        Submitted by <strong><c:out value="${tipItem.authorUsername}" /></strong> on <c:out value="${tipItem.createdAtFormatted}" />
                                    </div>
                                </div>
                                <div>
                                    <c:choose>
                                        <c:when test="${tipItem.status.name() == 'PENDING'}">
                                            <span class="badge bg-warning text-dark px-3 py-2 rounded-pill"><i class="bi bi-hourglass-split me-1"></i> Pending Review</span>
                                        </c:when>
                                        <c:when test="${tipItem.status.name() == 'APPROVED'}">
                                            <span class="badge bg-success text-white px-3 py-2 rounded-pill"><i class="bi bi-check-circle me-1"></i> Approved</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger text-white px-3 py-2 rounded-pill"><i class="bi bi-x-octagon me-1"></i> Rejected</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Content preview -->
                            <div class="p-3 bg-light rounded mb-3 text-dark font-monospace small" style="white-space: pre-wrap; max-height: 160px; overflow-y: auto;">
                                <c:out value="${tipItem.content}" />
                            </div>

                            <c:if test="${not empty tipItem.imageUrl}">
                                <div class="mb-3">
                                    <img src="<c:out value='${tipItem.imageUrl}' />" alt="Attachment" class="img-thumbnail rounded" style="max-height: 120px;" />
                                </div>
                            </c:if>

                            <!-- Review Audit details if reviewed -->
                            <c:if test="${not empty tipItem.reviewedBy}">
                                <div class="alert alert-secondary py-2 px-3 small mb-3">
                                    <strong>Moderator:</strong> <c:out value="${tipItem.reviewedBy}" /> &bull;
                                    <strong>Reviewed on:</strong> <c:out value="${tipItem.reviewedAtFormatted}" />
                                    <c:if test="${not empty tipItem.rejectionReason}">
                                        <br/><strong>Rejection Reason:</strong> <c:out value="${tipItem.rejectionReason}" />
                                    </c:if>
                                </div>
                            </c:if>

                            <!-- Moderation Action Buttons -->
                            <div class="d-flex flex-wrap gap-2 justify-content-end align-items-center">
                                <a href="${pageContext.request.contextPath}/tips/view?id=${tipItem.id}" target="_blank" class="btn btn-sm btn-outline-secondary me-auto">
                                    <i class="bi bi-box-arrow-up-right me-1"></i> Public View
                                </a>

                                <c:if test="${tipItem.status.name() != 'APPROVED'}">
                                    <form action="${pageContext.request.contextPath}/admin/moderation/approve" method="post" class="d-inline">
                                        <input type="hidden" name="_csrf" value="${csrfToken}" />
                                        <input type="hidden" name="type" value="tip" />
                                        <input type="hidden" name="id" value="${tipItem.id}" />
                                        <button type="submit" class="btn btn-sm btn-success px-3">
                                            <i class="bi bi-check-lg me-1"></i> Approve & Publish
                                        </button>
                                    </form>
                                </c:if>

                                <c:if test="${tipItem.status.name() != 'REJECTED'}">
                                    <button type="button" class="btn btn-sm btn-outline-danger px-3" data-bs-toggle="modal" data-bs-target="#rejectModal${tipItem.id}">
                                        <i class="bi bi-x-lg me-1"></i> Reject...
                                    </button>

                                    <!-- Rejection Modal -->
                                    <div class="modal fade" id="rejectModal${tipItem.id}" tabindex="-1" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content border-0 shadow">
                                                <form action="${pageContext.request.contextPath}/admin/moderation/reject" method="post">
                                                    <input type="hidden" name="_csrf" value="${csrfToken}" />
                                                    <input type="hidden" name="type" value="tip" />
                                                    <input type="hidden" name="id" value="${tipItem.id}" />
                                                    <div class="modal-header bg-danger text-white">
                                                        <h5 class="modal-title fw-bold"><i class="bi bi-x-circle me-1"></i> Reject Gardening Tip</h5>
                                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body p-4">
                                                        <p class="text-dark mb-2">You are rejecting: <strong><c:out value="${tipItem.title}" /></strong></p>
                                                        <div class="mb-3">
                                                            <label for="reason${tipItem.id}" class="form-label fw-semibold">Reason for Rejection <span class="text-danger">*</span></label>
                                                            <textarea class="form-control" id="reason${tipItem.id}" name="reason" rows="3" required placeholder="Explain why this content violated community guidelines or horticultural standards..."></textarea>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer bg-light">
                                                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                                        <button type="submit" class="btn btn-danger"><i class="bi bi-shield-x me-1"></i> Confirm Rejection</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Pagination -->
                <c:if test="${tipsPage.totalPages > 1}">
                    <nav aria-label="Moderation Pagination" class="mb-4">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${!tipsPage.hasPreviousPage() ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/moderation?page=${tipsPage.currentPage - 1}&size=${tipsPage.pageSize}&status=${selectedStatus.name()}&q=${searchQuery != null ? searchQuery : ''}">&laquo; Previous</a>
                            </li>
                            <c:forEach begin="1" end="${tipsPage.totalPages}" var="p">
                                <li class="page-item ${p == tipsPage.currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/admin/moderation?page=${p}&size=${tipsPage.pageSize}&status=${selectedStatus.name()}&q=${searchQuery != null ? searchQuery : ''}">${p}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${!tipsPage.hasNextPage() ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/moderation?page=${tipsPage.currentPage + 1}&size=${tipsPage.pageSize}&status=${selectedStatus.name()}&q=${searchQuery != null ? searchQuery : ''}">Next &raquo;</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
