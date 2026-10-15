<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="container py-4">
    <!-- Header & Action -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h2 fw-bold text-dark mb-1">Community Discussion Forums</h1>
            <p class="text-muted mb-0">Ask questions, troubleshoot plant problems, and exchange ideas.</p>
        </div>
        <div class="mt-3 mt-md-0">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <button type="button" class="btn btn-botanical" data-bs-toggle="modal" data-bs-target="#newDiscussionModal">
                        <i class="bi bi-chat-dots me-1"></i> Start New Topic
                    </button>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-botanical">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Sign In to Post
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Search & Filter Bar -->
    <div class="card card-botanical p-3 mb-4 border-0 shadow-sm">
        <form action="${pageContext.request.contextPath}/discussions" method="get" class="row g-2 align-items-center">
            <c:if test="${not empty selectedCategory}">
                <input type="hidden" name="category" value="${selectedCategory}" />
            </c:if>

            <div class="col-md-10">
                <div class="input-group">
                    <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                    <input type="text" class="form-control border-start-0" name="q" value="<c:out value='${searchQuery}' />"
                           placeholder="Search discussion topics by keyword or plant issue...">
                </div>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-botanical w-100">Search</button>
            </div>
        </form>

        <!-- Category Pills -->
        <div class="d-flex flex-wrap gap-2 mt-3 pt-3 border-top">
            <a href="${pageContext.request.contextPath}/discussions?q=<c:out value='${searchQuery}' />"
               class="category-pill ${empty selectedCategory ? 'active' : ''}">
                <i class="bi bi-chat-square-dots"></i> All Topics
            </a>
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/discussions?category=${cat.id}&q=<c:out value='${searchQuery}' />"
                   class="category-pill ${selectedCategory == cat.id ? 'active' : ''}">
                    <i class="bi ${cat.icon}"></i> <c:out value="${cat.name}" />
                </a>
            </c:forEach>
        </div>
    </div>

    <!-- Discussion Threads List -->
    <c:choose>
        <c:when test="${not empty pageResult.items}">
            <div class="row g-3 mb-5">
                <c:forEach var="disc" items="${pageResult.items}">
                    <div class="col-12">
                        <div class="card card-botanical p-3 p-md-4 shadow-sm">
                            <div class="row align-items-center g-3">
                                <div class="col-md-8">
                                    <span class="badge badge-botanical mb-2"><c:out value="${disc.categoryName}" /></span>
                                    <h4 class="h5 fw-bold mb-1">
                                        <a href="${pageContext.request.contextPath}/discussions/view?id=${disc.id}" class="text-dark hover-primary text-decoration-none">
                                            <c:out value="${disc.title}" />
                                        </a>
                                    </h4>
                                    <div class="small text-muted">
                                        Started by <strong><c:out value="${disc.authorName}" /></strong> &bull;
                                        <fmt:formatDate value="${disc.createdAt}" pattern="MMM dd, yyyy" />
                                    </div>
                                </div>
                                <div class="col-md-4 text-md-end d-flex align-items-center justify-content-md-end gap-3">
                                    <div class="text-center px-3 py-1 bg-light rounded">
                                        <div class="fw-bold text-success fs-5">${disc.commentCount}</div>
                                        <div class="small text-muted">Replies</div>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/discussions/view?id=${disc.id}" class="btn btn-botanical btn-sm">
                                        View Thread
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Pagination Bar -->
            <c:if test="${pageResult.totalPages > 1}">
                <nav aria-label="Discussions pagination" class="mb-5">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${!pageResult.hasPrevious() ? 'disabled' : ''}">
                            <a class="page-link text-success"
                               href="${pageContext.request.contextPath}/discussions?page=${pageResult.previousPage}&category=${selectedCategory}&q=<c:out value='${searchQuery}' />">
                                <i class="bi bi-chevron-left"></i> Previous
                            </a>
                        </li>
                        <c:forEach var="pageNum" items="${pageResult.navigationPages}">
                            <li class="page-item ${pageNum == pageResult.currentPage ? 'active' : ''}">
                                <a class="page-link ${pageNum == pageResult.currentPage ? 'bg-success border-success text-white' : 'text-success'}"
                                   href="${pageContext.request.contextPath}/discussions?page=${pageNum}&category=${selectedCategory}&q=<c:out value='${searchQuery}' />">
                                    ${pageNum}
                                </a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${!pageResult.hasNext() ? 'disabled' : ''}">
                            <a class="page-link text-success"
                               href="${pageContext.request.contextPath}/discussions?page=${pageResult.nextPage}&category=${selectedCategory}&q=<c:out value='${searchQuery}' />">
                                Next <i class="bi bi-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="card card-botanical text-center p-5 border-0 shadow-sm my-5">
                <i class="bi bi-chat-square-dots display-3 text-muted mb-3"></i>
                <h4 class="fw-bold text-dark">No Discussions Found</h4>
                <p class="text-muted mx-auto mb-4" style="max-width: 500px;">
                    No threads match your filter criteria. Be the first to start a conversation in this topic!
                </p>
                <div>
                    <a href="${pageContext.request.contextPath}/discussions" class="btn btn-botanical">Browse All Topics</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Modal: New Discussion Topic -->
<c:if test="${not empty sessionScope.currentUser}">
    <div class="modal fade" id="newDiscussionModal" tabindex="-1" aria-labelledby="newDiscussionModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content border-0 shadow-lg">
                <form action="${pageContext.request.contextPath}/gardener/discussions/create" method="post">
                    <input type="hidden" name="_csrf" value="${csrfToken}" />
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title fw-bold" id="newDiscussionModalLabel">
                            <i class="bi bi-chat-plus me-2"></i> Start a Discussion Thread
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
                                   placeholder="e.g. Yellow leaves on indoor Monstera: root rot or light issue?">
                        </div>
                        <div class="mb-3">
                            <label for="content" class="form-label fw-semibold">Description / Question</label>
                            <textarea class="form-control" id="content" name="content" rows="6" required
                                      placeholder="Provide details about your soil, watering schedule, sunlight exposure, and observations..."></textarea>
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
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
