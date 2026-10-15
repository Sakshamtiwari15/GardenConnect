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
                <h1 class="h3 fw-bold text-dark mb-1">My Discussion Topics</h1>
                <p class="text-muted mb-0">Conversations and community questions you have initiated.</p>
            </div>
            <div class="mt-3 mt-md-0">
                <button type="button" class="btn btn-botanical" data-bs-toggle="modal" data-bs-target="#newDiscussionModal">
                    <i class="bi bi-chat-plus me-1"></i> Start New Topic
                </button>
            </div>
        </div>

        <div class="card card-botanical border-0 shadow-sm overflow-hidden">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="min-width: 250px;">Topic Title & Category</th>
                            <th scope="col">Replies</th>
                            <th scope="col">Date Posted</th>
                            <th scope="col" class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty pageResult.items}">
                                <c:forEach var="disc" items="${pageResult.items}">
                                    <tr>
                                        <td>
                                            <div class="fw-bold text-dark">
                                                <a href="${pageContext.request.contextPath}/discussions/view?id=${disc.id}" class="text-dark text-decoration-none">
                                                    <c:out value="${disc.title}" />
                                                </a>
                                            </div>
                                            <span class="badge badge-botanical"><c:out value="${disc.categoryName}" /></span>
                                        </td>
                                        <td>
                                            <span class="badge bg-light text-dark border">
                                                <i class="bi bi-chat-left-text me-1 text-success"></i> ${disc.commentCount} replies
                                            </span>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${disc.createdAt}" pattern="MMM dd, yyyy" />
                                        </td>
                                        <td class="text-end">
                                            <a href="${pageContext.request.contextPath}/discussions/view?id=${disc.id}" class="btn btn-sm btn-outline-success">
                                                View Thread
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="4" class="text-center py-5 text-muted">
                                        <i class="bi bi-chat-dots display-4 mb-2 d-inline-block"></i>
                                        <p class="mb-2">You haven't posted any discussion topics yet.</p>
                                        <button type="button" class="btn btn-botanical btn-sm" data-bs-toggle="modal" data-bs-target="#newDiscussionModal">
                                            Ask the Community
                                        </button>
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
                    <nav aria-label="Gardener discussions pagination">
                        <ul class="pagination pagination-sm justify-content-center mb-0">
                            <li class="page-item ${!pageResult.hasPrevious() ? 'disabled' : ''}">
                                <a class="page-link text-success" href="${pageContext.request.contextPath}/gardener/discussions?page=${pageResult.previousPage}">Previous</a>
                            </li>
                            <c:forEach var="pageNum" items="${pageResult.navigationPages}">
                                <li class="page-item ${pageNum == pageResult.currentPage ? 'active' : ''}">
                                    <a class="page-link ${pageNum == pageResult.currentPage ? 'bg-success border-success text-white' : 'text-success'}"
                                       href="${pageContext.request.contextPath}/gardener/discussions?page=${pageNum}">${pageNum}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${!pageResult.hasNext() ? 'disabled' : ''}">
                                <a class="page-link text-success" href="${pageContext.request.contextPath}/gardener/discussions?page=${pageResult.nextPage}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </main>
</div>

<!-- Modal: New Discussion -->
<div class="modal fade" id="newDiscussionModal" tabindex="-1" aria-labelledby="newDiscussionModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow-lg">
            <form action="${pageContext.request.contextPath}/gardener/discussions/create" method="post">
                <input type="hidden" name="_csrf" value="${csrfToken}" />
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title fw-bold" id="newDiscussionModalLabel">
                        <i class="bi bi-chat-plus me-2"></i> Start a Discussion Topic
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="categoryId" class="form-label fw-semibold">Category</label>
                        <select class="form-select" id="categoryId" name="categoryId" required>
                            <option value="" disabled selected>Select a relevant category...</option>
                            <c:forEach var="c" items="${categories}">
                                <option value="${c.id}"><c:out value="${c.name}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="title" class="form-label fw-semibold">Topic Title</label>
                        <input type="text" class="form-control" id="title" name="title" required
                               placeholder="e.g. Trellising indeterminate tomatoes in small raised beds">
                    </div>
                    <div class="mb-3">
                        <label for="content" class="form-label fw-semibold">Description / Question</label>
                        <textarea class="form-control" id="content" name="content" rows="6" required
                                  placeholder="Describe your plant questions, observations, or ideas..."></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-botanical">Post Topic</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
