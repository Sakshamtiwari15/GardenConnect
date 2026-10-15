<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="dashboard-layout">
    <!-- Gardener Sidebar -->
    <jsp:include page="/WEB-INF/views/fragments/gardener-sidebar.jsp" />

    <!-- Dashboard Main Content -->
    <main class="dashboard-content">
        <!-- Welcome Banner -->
        <div class="card card-botanical p-4 p-md-5 mb-4 border-0 shadow-sm text-white"
             style="background: linear-gradient(135deg, var(--gc-primary), var(--gc-forest-dark));">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <span class="badge bg-warning text-dark px-3 py-1 rounded-pill fw-bold mb-2">Gardener Portal</span>
                    <h1 class="display-6 fw-bold mb-2">Welcome back, <c:out value="${sessionScope.currentUser.fullName}" />!</h1>
                    <p class="mb-0 text-white-50">
                        Track your crops, share your growing successes, and connect with fellow gardeners.
                    </p>
                </div>
                <div class="col-md-4 text-md-end mt-3 mt-md-0 d-flex flex-wrap gap-2 justify-content-md-end">
                    <a href="${pageContext.request.contextPath}/gardener/projects/create" class="btn btn-botanical-accent">
                        <i class="bi bi-plus-lg me-1"></i> New Project
                    </a>
                    <a href="${pageContext.request.contextPath}/gardener/tips/create" class="btn btn-outline-light">
                        <i class="bi bi-pencil-square me-1"></i> Share Tip
                    </a>
                </div>
            </div>
        </div>

        <!-- KPI Statistic Metric Cards (Real Database Records) -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Tips Shared</span>
                            <h3 class="fw-bold mb-0 text-dark mt-1">${stats.userTipsCount}</h3>
                        </div>
                        <div class="bg-success bg-opacity-10 text-success p-3 rounded-circle">
                            <i class="bi bi-journal-text fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Active Projects</span>
                            <h3 class="fw-bold mb-0 text-primary mt-1">${stats.userActiveProjectsCount}</h3>
                        </div>
                        <div class="bg-primary bg-opacity-10 text-primary p-3 rounded-circle">
                            <i class="bi bi-kanban fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Completed Crops</span>
                            <h3 class="fw-bold mb-0 text-success mt-1">${stats.userCompletedProjectsCount}</h3>
                        </div>
                        <div class="bg-success bg-opacity-10 text-success p-3 rounded-circle">
                            <i class="bi bi-check2-circle fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card card-botanical p-3 border-0 shadow-sm">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-bold text-uppercase">Discussions</span>
                            <h3 class="fw-bold mb-0 text-dark mt-1">${stats.userDiscussionsCount}</h3>
                        </div>
                        <div class="bg-warning bg-opacity-10 text-warning p-3 rounded-circle">
                            <i class="bi bi-chat-dots fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4 mb-4">
            <!-- Active Gardening Projects Progress Section -->
            <div class="col-lg-7">
                <div class="card card-botanical p-4 border-0 shadow-sm h-100">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
                            <i class="bi bi-kanban text-success"></i> My Gardening Projects
                        </h5>
                        <a href="${pageContext.request.contextPath}/gardener/projects" class="btn btn-sm btn-outline-success">
                            View All (${stats.totalProjects})
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${not empty recentProjects}">
                            <div class="d-flex flex-column gap-3">
                                <c:forEach var="proj" items="${recentProjects}">
                                    <div class="p-3 bg-light rounded-3 border">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <div>
                                                <h6 class="fw-bold text-dark mb-0"><c:out value="${proj.title}" /></h6>
                                                <small class="text-muted"><c:out value="${proj.plantCropType != null ? proj.plantCropType : 'General Crop'}" /></small>
                                            </div>
                                            <span class="badge ${proj.status.badgeClass}"><c:out value="${proj.status.displayName}" /></span>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center small text-muted mb-1">
                                            <span>Progress</span>
                                            <span class="fw-bold">${proj.progressPercentage}%</span>
                                        </div>
                                        <div class="progress progress-botanical">
                                            <div class="progress-bar" role="progressbar" style="width: ${proj.progressPercentage}%"
                                                 aria-valuenow="${proj.progressPercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-5 text-muted">
                                <i class="bi bi-flower1 display-4 mb-2 d-inline-block text-muted"></i>
                                <p class="mb-3">You haven't tracked any gardening projects yet.</p>
                                <a href="${pageContext.request.contextPath}/gardener/projects/create" class="btn btn-botanical btn-sm">
                                    Start Your First Project
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Recent Tips by Gardener -->
            <div class="col-lg-5">
                <div class="card card-botanical p-4 border-0 shadow-sm h-100">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
                            <i class="bi bi-journal-text text-success"></i> My Tips & Status
                        </h5>
                        <a href="${pageContext.request.contextPath}/gardener/tips" class="btn btn-sm btn-outline-success">
                            Manage
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${not empty userTips}">
                            <div class="d-flex flex-column gap-3">
                                <c:forEach var="tip" items="${userTips}">
                                    <div class="p-3 bg-light rounded-3 border">
                                        <div class="d-flex justify-content-between align-items-start mb-1">
                                            <h6 class="fw-bold text-dark mb-0 text-truncate" style="max-width: 220px;">
                                                <a href="${pageContext.request.contextPath}/tips/view?id=${tip.id}" class="text-dark text-decoration-none">
                                                    <c:out value="${tip.title}" />
                                                </a>
                                            </h6>
                                            <span class="badge ${tip.moderationStatus.badgeClass}">
                                                <c:out value="${tip.moderationStatus.label}" />
                                            </span>
                                        </div>
                                        <div class="d-flex justify-content-between align-items-center small text-muted">
                                            <span><c:out value="${tip.categoryName}" /></span>
                                            <span><i class="bi bi-eye me-1"></i>${tip.viewCount} views</span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-5 text-muted">
                                <i class="bi bi-journal-plus display-4 mb-2 d-inline-block text-muted"></i>
                                <p class="mb-3">Share your gardening knowledge with the community.</p>
                                <a href="${pageContext.request.contextPath}/gardener/tips/create" class="btn btn-botanical btn-sm">
                                    Create a Tip
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Community Discussions Feed -->
        <div class="card card-botanical p-4 border-0 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
                    <i class="bi bi-chat-square-dots text-success"></i> Community Discussions You Might Like
                </h5>
                <a href="${pageContext.request.contextPath}/discussions" class="btn btn-sm btn-outline-success">
                    Explore Forums
                </a>
            </div>

            <div class="row g-3">
                <c:forEach var="disc" items="${recentDiscussions}">
                    <div class="col-md-6">
                        <div class="p-3 bg-light rounded-3 border h-100 d-flex flex-column justify-content-between">
                            <div>
                                <span class="badge badge-botanical mb-1"><c:out value="${disc.categoryName}" /></span>
                                <h6 class="fw-bold mb-1">
                                    <a href="${pageContext.request.contextPath}/discussions/view?id=${disc.id}" class="text-dark text-decoration-none">
                                        <c:out value="${disc.title}" />
                                    </a>
                                </h6>
                            </div>
                            <div class="d-flex justify-content-between align-items-center small text-muted mt-2 pt-2 border-top">
                                <span>By <c:out value="${disc.authorName}" /></span>
                                <span><i class="bi bi-chat-left-text me-1"></i>${disc.commentCount} replies</span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
