<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/fragments/gardener-sidebar.jsp" />

    <main class="dashboard-content">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
            <div>
                <h1 class="h3 fw-bold text-dark mb-1">My Gardening Tips</h1>
                <p class="text-muted mb-0">Track the review status and engagement of your submitted guides.</p>
            </div>
            <div class="mt-3 mt-md-0">
                <a href="${pageContext.request.contextPath}/gardener/tips/create" class="btn btn-botanical">
                    <i class="bi bi-plus-lg me-1"></i> Share a New Tip
                </a>
            </div>
        </div>

        <div class="card card-botanical border-0 shadow-sm overflow-hidden">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="min-width: 250px;">Title & Category</th>
                            <th scope="col">Status</th>
                            <th scope="col">Views</th>
                            <th scope="col">Date Submitted</th>
                            <th scope="col" class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty pageResult.items}">
                                <c:forEach var="tip" items="${pageResult.items}">
                                    <tr>
                                        <td>
                                            <div class="fw-bold text-dark">
                                                <a href="${pageContext.request.contextPath}/tips/view?id=${tip.id}" class="text-dark text-decoration-none">
                                                    <c:out value="${tip.title}" />
                                                </a>
                                            </div>
                                            <span class="badge badge-botanical"><c:out value="${tip.categoryName}" /></span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${tip.moderationStatus == 'APPROVED'}">
                                                    <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i> Approved</span>
                                                </c:when>
                                                <c:when test="${tip.moderationStatus == 'PENDING'}">
                                                    <span class="badge bg-warning text-dark"><i class="bi bi-hourglass-split me-1"></i> In Review</span>
                                                </c:when>
                                                <c:when test="${tip.moderationStatus == 'REJECTED'}">
                                                    <span class="badge bg-danger" data-bs-toggle="tooltip" title="Reason: <c:out value='${tip.rejectionReason}' />">
                                                        <i class="bi bi-x-circle me-1"></i> Rejected
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                            <c:if test="${not empty tip.rejectionReason}">
                                                <div class="small text-danger mt-1">
                                                    <i class="bi bi-info-circle"></i> <c:out value="${tip.rejectionReason}" />
                                                </div>
                                            </c:if>
                                        </td>
                                        <td>
                                            <i class="bi bi-eye text-muted me-1"></i>${tip.viewCount}
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${tip.createdAt}" pattern="MMM dd, yyyy" />
                                        </td>
                                        <td class="text-end">
                                            <div class="d-inline-flex gap-2">
                                                <a href="${pageContext.request.contextPath}/gardener/tips/edit?id=${tip.id}"
                                                   class="btn btn-sm btn-outline-secondary" title="Edit Tip">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <form action="${pageContext.request.contextPath}/gardener/tips/delete" method="post" class="d-inline">
                                                    <input type="hidden" name="_csrf" value="${csrfToken}" />
                                                    <input type="hidden" name="id" value="${tip.id}" />
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete Tip"
                                                            data-confirm="Are you sure you want to permanently delete this tip?">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center py-5 text-muted">
                                        <i class="bi bi-journal-x display-4 mb-2 d-inline-block"></i>
                                        <p class="mb-2">You haven't authored any gardening tips yet.</p>
                                        <a href="${pageContext.request.contextPath}/gardener/tips/create" class="btn btn-botanical btn-sm">
                                            Share Your First Tip
                                        </a>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <!-- Pagination Bar -->
            <c:if test="${pageResult.totalPages > 1}">
                <div class="p-3 border-top">
                    <nav aria-label="Gardener tips pagination">
                        <ul class="pagination pagination-sm justify-content-center mb-0">
                            <li class="page-item ${!pageResult.hasPrevious() ? 'disabled' : ''}">
                                <a class="page-link text-success" href="${pageContext.request.contextPath}/gardener/tips?page=${pageResult.previousPage}">Previous</a>
                            </li>
                            <c:forEach var="pageNum" items="${pageResult.navigationPages}">
                                <li class="page-item ${pageNum == pageResult.currentPage ? 'active' : ''}">
                                    <a class="page-link ${pageNum == pageResult.currentPage ? 'bg-success border-success text-white' : 'text-success'}"
                                       href="${pageContext.request.contextPath}/gardener/tips?page=${pageNum}">${pageNum}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${!pageResult.hasNext() ? 'disabled' : ''}">
                                <a class="page-link text-success" href="${pageContext.request.contextPath}/gardener/tips?page=${pageResult.nextPage}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
