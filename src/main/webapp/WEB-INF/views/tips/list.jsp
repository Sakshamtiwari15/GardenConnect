<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="container py-4">
    <!-- Breadcrumb & Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h2 fw-bold text-dark mb-1">Gardening Tips & Techniques</h1>
            <p class="text-muted mb-0">Practical wisdom contributed by gardeners, for gardeners.</p>
        </div>
        <div class="mt-3 mt-md-0">
            <a href="${pageContext.request.contextPath}/gardener/tips/create" class="btn btn-botanical">
                <i class="bi bi-plus-circle me-1"></i> Share a Gardening Tip
            </a>
        </div>
    </div>

    <!-- Search & Filter Controls Card -->
    <div class="card card-botanical p-3 mb-4 border-0 shadow-sm">
        <form action="${pageContext.request.contextPath}/tips" method="get" class="row g-2 align-items-center">
            <c:if test="${not empty selectedCategory}">
                <input type="hidden" name="category" value="${selectedCategory}" />
            </c:if>

            <div class="col-md-7">
                <div class="input-group">
                    <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                    <input type="text" class="form-control border-start-0" name="q" value="<c:out value='${searchQuery}' />"
                           placeholder="Search tips by plant, technique, or pest problem...">
                </div>
            </div>

            <div class="col-md-3">
                <select class="form-select" name="sort" onchange="this.form.submit()">
                    <option value="newest" ${currentSort == 'newest' ? 'selected' : ''}>Newest First</option>
                    <option value="popular" ${currentSort == 'popular' ? 'selected' : ''}>Most Popular (Views)</option>
                    <option value="oldest" ${currentSort == 'oldest' ? 'selected' : ''}>Oldest First</option>
                </select>
            </div>

            <div class="col-md-2">
                <button type="submit" class="btn btn-botanical w-100">Filter</button>
            </div>
        </form>

        <!-- Category Pills Filter Bar -->
        <div class="d-flex flex-wrap gap-2 mt-3 pt-3 border-top">
            <a href="${pageContext.request.contextPath}/tips?q=<c:out value='${searchQuery}' />&sort=${currentSort}"
               class="category-pill ${empty selectedCategory ? 'active' : ''}">
                <i class="bi bi-grid-fill"></i> All Topics
            </a>
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/tips?category=${cat.id}&q=<c:out value='${searchQuery}' />&sort=${currentSort}"
                   class="category-pill ${selectedCategory == cat.id ? 'active' : ''}">
                    <i class="bi ${cat.icon}"></i> <c:out value="${cat.name}" />
                </a>
            </c:forEach>
        </div>
    </div>

    <!-- Results Grid -->
    <c:choose>
        <c:when test="${not empty pageResult.items}">
            <div class="row g-4 mb-5">
                <c:forEach var="tip" items="${pageResult.items}">
                    <div class="col-lg-4 col-md-6">
                        <div class="card card-botanical h-100 shadow-sm">
                            <c:if test="${not empty tip.imageUrl}">
                                <img src="${tip.imageUrl}" class="card-img-top" alt="${tip.title}" style="height: 200px; object-fit: cover;">
                            </c:if>
                            <div class="card-body d-flex flex-column p-4">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="badge badge-botanical"><c:out value="${tip.categoryName}" /></span>
                                    <small class="text-muted"><i class="bi bi-eye me-1"></i>${tip.viewCount}</small>
                                </div>
                                <h5 class="card-title fw-bold mb-2">
                                    <a href="${pageContext.request.contextPath}/tips/view?id=${tip.id}" class="text-dark hover-primary text-decoration-none">
                                        <c:out value="${tip.title}" />
                                    </a>
                                </h5>
                                <p class="card-text text-muted flex-grow-1 small">
                                    <c:out value="${tip.summary != null ? tip.summary : tip.content}" />
                                </p>
                                <hr class="my-3 opacity-25">
                                <div class="d-flex justify-content-between align-items-center small">
                                    <span class="text-muted">
                                        <i class="bi bi-person me-1"></i><strong><c:out value="${tip.authorName}" /></strong>
                                    </span>
                                    <a href="${pageContext.request.contextPath}/tips/view?id=${tip.id}" class="btn btn-sm btn-outline-success">
                                        Read Guide
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Pagination Bar -->
            <c:if test="${pageResult.totalPages > 1}">
                <nav aria-label="Tips navigation" class="mb-5">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${!pageResult.hasPrevious() ? 'disabled' : ''}">
                            <a class="page-link text-success"
                               href="${pageContext.request.contextPath}/tips?page=${pageResult.previousPage}&category=${selectedCategory}&q=<c:out value='${searchQuery}' />&sort=${currentSort}">
                                <i class="bi bi-chevron-left"></i> Previous
                            </a>
                        </li>
                        <c:forEach var="pageNum" items="${pageResult.navigationPages}">
                            <li class="page-item ${pageNum == pageResult.currentPage ? 'active' : ''}">
                                <a class="page-link ${pageNum == pageResult.currentPage ? 'bg-success border-success text-white' : 'text-success'}"
                                   href="${pageContext.request.contextPath}/tips?page=${pageNum}&category=${selectedCategory}&q=<c:out value='${searchQuery}' />&sort=${currentSort}">
                                    ${pageNum}
                                </a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${!pageResult.hasNext() ? 'disabled' : ''}">
                            <a class="page-link text-success"
                               href="${pageContext.request.contextPath}/tips?page=${pageResult.nextPage}&category=${selectedCategory}&q=<c:out value='${searchQuery}' />&sort=${currentSort}">
                                Next <i class="bi bi-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </c:when>
        <c:otherwise>
            <!-- Clean Empty State -->
            <div class="card card-botanical text-center p-5 border-0 shadow-sm my-5">
                <i class="bi bi-search display-3 text-muted mb-3"></i>
                <h4 class="fw-bold text-dark">No Gardening Tips Found</h4>
                <p class="text-muted mx-auto mb-4" style="max-width: 500px;">
                    We couldn't find any tips matching your current filter or search criteria. Try broadening your keywords or browse all categories.
                </p>
                <div>
                    <a href="${pageContext.request.contextPath}/tips" class="btn btn-botanical">Clear Filters</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
