<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<!-- Hero Section -->
<section class="hero-section text-center text-lg-start">
    <div class="container py-4">
        <div class="row align-items-center g-5">
            <div class="col-lg-7">
                <span class="badge bg-warning text-dark px-3 py-2 rounded-pill fw-bold mb-3">
                    <i class="bi bi-stars me-1"></i> Growing Together Since 2026
                </span>
                <h1 class="display-4 fw-extrabold text-white mb-3">
                    Cultivate Knowledge, Share Your Harvest.
                </h1>
                <p class="hero-subtitle mb-4">
                    Join thousands of urban farmers, balcony experimenters, and master gardeners. Share proven growing techniques, solve plant problems together, and track your garden projects.
                </p>
                <div class="d-flex flex-wrap gap-3 justify-content-center justify-content-lg-start">
                    <a href="${pageContext.request.contextPath}/tips" class="btn btn-botanical-accent btn-lg px-4">
                        <i class="bi bi-compass me-2"></i> Explore Gardening Tips
                    </a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-light btn-lg px-4">
                        <i class="bi bi-person-plus me-2"></i> Join Our Community
                    </a>
                </div>
            </div>

            <!-- Database-Driven Live Metrics Counters -->
            <div class="col-lg-5">
                <div class="row g-3">
                    <div class="col-6">
                        <div class="stat-box">
                            <i class="bi bi-people fs-2 text-warning mb-2"></i>
                            <div class="stat-number"><c:out value="${stats.totalGardeners}" /></div>
                            <div class="stat-label">Active Gardeners</div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="stat-box">
                            <i class="bi bi-journal-check fs-2 text-warning mb-2"></i>
                            <div class="stat-number"><c:out value="${stats.approvedTipsCount}" /></div>
                            <div class="stat-label">Curated Tips</div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="stat-box">
                            <i class="bi bi-chat-dots fs-2 text-warning mb-2"></i>
                            <div class="stat-number"><c:out value="${stats.totalDiscussions}" /></div>
                            <div class="stat-label">Discussions</div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="stat-box">
                            <i class="bi bi-kanban fs-2 text-warning mb-2"></i>
                            <div class="stat-number"><c:out value="${stats.totalProjects}" /></div>
                            <div class="stat-label">Garden Projects</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Gardening Categories Grid -->
<section class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <span class="text-uppercase fw-bold text-success small tracking-wider">Explore By Topic</span>
            <h2 class="display-6 fw-bold">Cultivation Categories</h2>
            <p class="text-muted">Discover practical techniques tailored to your plants and growing space</p>
        </div>

        <div class="row g-4">
            <c:forEach var="cat" items="${categories}">
                <div class="col-lg-3 col-md-4 col-sm-6">
                    <a href="${pageContext.request.contextPath}/tips?category=${cat.id}" class="text-decoration-none">
                        <div class="card card-botanical h-100 p-3 text-center border-0 shadow-sm">
                            <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle mx-auto p-3 mb-3" style="width: 64px; height: 64px;">
                                <i class="bi ${cat.icon} fs-3"></i>
                            </div>
                            <h5 class="card-title fw-bold text-dark mb-1"><c:out value="${cat.name}" /></h5>
                            <p class="card-text text-muted small"><c:out value="${cat.description}" /></p>
                        </div>
                    </a>
                </div>
            </c:forEach>
        </div>
    </div>
</section>

<!-- Featured Gardening Tips -->
<section class="py-5 bg-white border-top border-bottom">
    <div class="container">
        <div class="d-flex flex-wrap justify-content-between align-items-end mb-4">
            <div>
                <span class="text-uppercase fw-bold text-success small">Field-Tested Advice</span>
                <h2 class="display-6 fw-bold mb-0">Featured Gardening Tips</h2>
            </div>
            <a href="${pageContext.request.contextPath}/tips" class="btn btn-botanical-outline mt-3 mt-md-0">
                View All Tips <i class="bi bi-arrow-right ms-1"></i>
            </a>
        </div>

        <div class="row g-4">
            <c:choose>
                <c:when test="${not empty featuredTips}">
                    <c:forEach var="tip" items="${featuredTips}">
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
                                    <div class="d-flex justify-content-between align-items-center">
                                        <small class="text-muted fw-semibold">
                                            <i class="bi bi-person me-1"></i><c:out value="${tip.authorName}" />
                                        </small>
                                        <a href="${pageContext.request.contextPath}/tips/view?id=${tip.id}" class="btn btn-sm btn-outline-success">
                                            Read More
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="col-12 text-center py-5">
                        <i class="bi bi-flower2 display-4 text-muted"></i>
                        <p class="text-muted mt-2">No gardening tips published yet. Be the first to share one!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<!-- Community Discussions Preview -->
<section class="py-5">
    <div class="container">
        <div class="d-flex flex-wrap justify-content-between align-items-end mb-4">
            <div>
                <span class="text-uppercase fw-bold text-success small">Community Q&A</span>
                <h2 class="display-6 fw-bold mb-0">Recent Discussions</h2>
            </div>
            <a href="${pageContext.request.contextPath}/discussions" class="btn btn-botanical-outline mt-3 mt-md-0">
                Join All Discussions <i class="bi bi-arrow-right ms-1"></i>
            </a>
        </div>

        <div class="row g-3">
            <c:forEach var="disc" items="${recentDiscussions}">
                <div class="col-12">
                    <div class="card card-botanical p-3 p-md-4">
                        <div class="row align-items-center g-3">
                            <div class="col-md-8">
                                <span class="badge badge-botanical mb-2"><c:out value="${disc.categoryName}" /></span>
                                <h5 class="fw-bold mb-1">
                                    <a href="${pageContext.request.contextPath}/discussions/view?id=${disc.id}" class="text-dark text-decoration-none">
                                        <c:out value="${disc.title}" />
                                    </a>
                                </h5>
                                <small class="text-muted">
                                    Started by <strong><c:out value="${disc.authorName}" /></strong>
                                </small>
                            </div>
                            <div class="col-md-4 text-md-end d-flex align-items-center justify-content-md-end gap-3">
                                <div class="text-center px-3 py-1 bg-light rounded">
                                    <div class="fw-bold text-success fs-5">${disc.commentCount}</div>
                                    <div class="small text-muted">Replies</div>
                                </div>
                                <a href="${pageContext.request.contextPath}/discussions/view?id=${disc.id}" class="btn btn-botanical btn-sm">
                                    Join Thread
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</section>

<!-- Call to Action Banner -->
<section class="py-5 bg-success bg-opacity-10">
    <div class="container text-center py-4">
        <i class="bi bi-tree fs-1 text-success mb-3 d-inline-block"></i>
        <h2 class="display-6 fw-bold text-dark mb-3">Ready to Watch Your Garden Flourish?</h2>
        <p class="lead text-muted mx-auto mb-4" style="max-width: 600px;">
            Join our welcoming network of plant enthusiasts. Track your personal gardening projects, document your harvest milestones, and exchange wisdom with passionate growers.
        </p>
        <a href="${pageContext.request.contextPath}/register" class="btn btn-botanical btn-lg px-5 shadow">
            Get Started Today &bull; Free Account
        </a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
