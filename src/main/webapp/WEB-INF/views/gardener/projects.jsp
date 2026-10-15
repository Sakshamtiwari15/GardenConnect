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
                <h1 class="h3 fw-bold text-dark mb-1">My Gardening Projects</h1>
                <p class="text-muted mb-0">Plan, track cultivation milestones, and measure harvest progress.</p>
            </div>
            <div class="mt-3 mt-md-0">
                <a href="${pageContext.request.contextPath}/gardener/projects/create" class="btn btn-botanical">
                    <i class="bi bi-plus-lg me-1"></i> Start New Project
                </a>
            </div>
        </div>

        <!-- Status Filter Pills -->
        <div class="d-flex flex-wrap gap-2 mb-4">
            <a href="${pageContext.request.contextPath}/gardener/projects"
               class="category-pill ${empty selectedStatus ? 'active' : ''}">
                All Projects
            </a>
            <c:forEach var="st" items="${statuses}">
                <a href="${pageContext.request.contextPath}/gardener/projects?status=${st.name()}"
                   class="category-pill ${selectedStatus == st ? 'active' : ''}">
                    <c:out value="${st.displayName}" />
                </a>
            </c:forEach>
        </div>

        <!-- Projects Cards Grid -->
        <c:choose>
            <c:when test="${not empty pageResult.items}">
                <div class="row g-4 mb-5">
                    <c:forEach var="proj" items="${pageResult.items}">
                        <div class="col-lg-6">
                            <div class="card card-botanical h-100 p-4 border-0 shadow-sm d-flex flex-column justify-content-between">
                                <div>
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <h5 class="fw-bold text-dark mb-1"><c:out value="${proj.title}" /></h5>
                                            <span class="badge bg-light text-dark border">
                                                <i class="bi bi-flower1 text-success me-1"></i>
                                                <c:out value="${proj.plantCropType != null ? proj.plantCropType : 'General Cultivation'}" />
                                            </span>
                                        </div>
                                        <span class="badge ${proj.status.badgeClass}"><c:out value="${proj.status.displayName}" /></span>
                                    </div>

                                    <c:if test="${not empty proj.description}">
                                        <p class="text-muted small mt-2 mb-3"><c:out value="${proj.description}" /></p>
                                    </c:if>

                                    <!-- Progress Bar with Percentage -->
                                    <div class="mb-3">
                                        <div class="d-flex justify-content-between align-items-center small mb-1">
                                            <span class="fw-semibold text-dark">Milestone Progress</span>
                                            <span class="fw-bold text-success">${proj.progressPercentage}%</span>
                                        </div>
                                        <div class="progress progress-botanical">
                                            <div class="progress-bar" role="progressbar" style="width: ${proj.progressPercentage}%"
                                                 aria-valuenow="${proj.progressPercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                                        </div>
                                    </div>

                                    <!-- Timeline Dates -->
                                    <div class="row g-2 small text-muted mb-3 bg-light p-2 rounded">
                                        <div class="col-6">
                                            <i class="bi bi-calendar-event me-1 text-success"></i>
                                            Started: <strong><fmt:formatDate value="${proj.startDate}" pattern="MMM dd, yyyy" /></strong>
                                        </div>
                                        <div class="col-6">
                                            <i class="bi bi-calendar-check me-1 text-primary"></i>
                                            Target: <strong><fmt:formatDate value="${proj.targetCompletionDate}" pattern="MMM dd, yyyy" /></strong>
                                        </div>
                                    </div>

                                    <c:if test="${not empty proj.notes}">
                                        <div class="small text-muted fst-italic mb-3">
                                            <i class="bi bi-stickies me-1"></i> <c:out value="${proj.notes}" />
                                        </div>
                                    </c:if>
                                </div>

                                <!-- Action Buttons -->
                                <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                                    <!-- Quick Update Progress Trigger -->
                                    <button type="button" class="btn btn-sm btn-outline-success"
                                            data-bs-toggle="modal" data-bs-target="#progressModal${proj.id}">
                                        <i class="bi bi-sliders me-1"></i> Quick Update
                                    </button>

                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/gardener/projects/edit?id=${proj.id}"
                                           class="btn btn-sm btn-outline-secondary">
                                            <i class="bi bi-pencil me-1"></i> Edit
                                        </a>
                                        <form action="${pageContext.request.contextPath}/gardener/projects/delete" method="post" class="d-inline">
                                            <input type="hidden" name="_csrf" value="${csrfToken}" />
                                            <input type="hidden" name="id" value="${proj.id}" />
                                            <button type="submit" class="btn btn-sm btn-outline-danger" data-confirm="Delete this gardening project?">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Quick Progress Modal for this Project -->
                        <div class="modal fade" id="progressModal${proj.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content border-0 shadow">
                                    <form action="${pageContext.request.contextPath}/gardener/projects/progress" method="post">
                                        <input type="hidden" name="_csrf" value="${csrfToken}" />
                                        <input type="hidden" name="id" value="${proj.id}" />

                                        <div class="modal-header bg-success text-white">
                                            <h5 class="modal-title fw-bold">Update Progress: <c:out value="${proj.title}" /></h5>
                                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Status</label>
                                                <select class="form-select" name="status">
                                                    <c:forEach var="st" items="${statuses}">
                                                        <option value="${st.name()}" ${proj.status == st ? 'selected' : ''}>
                                                            <c:out value="${st.displayName}" />
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label fw-semibold d-flex justify-content-between">
                                                    <span>Progress Percentage (0 - 100%)</span>
                                                    <span id="sliderVal${proj.id}" class="fw-bold text-success">${proj.progressPercentage}%</span>
                                                </label>
                                                <input type="range" class="form-range" name="progressPercentage"
                                                       min="0" max="100" value="${proj.progressPercentage}"
                                                       oninput="document.getElementById('sliderVal${proj.id}').textContent = this.value + '%'">
                                            </div>
                                        </div>
                                        <div class="modal-footer bg-light">
                                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-botanical">Save Progress</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Pagination Bar -->
                <c:if test="${pageResult.totalPages > 1}">
                    <nav aria-label="Projects pagination" class="mb-5">
                        <ul class="pagination pagination-sm justify-content-center mb-0">
                            <li class="page-item ${!pageResult.hasPrevious() ? 'disabled' : ''}">
                                <a class="page-link text-success"
                                   href="${pageContext.request.contextPath}/gardener/projects?page=${pageResult.previousPage}&status=${selectedStatus}">Previous</a>
                            </li>
                            <c:forEach var="pageNum" items="${pageResult.navigationPages}">
                                <li class="page-item ${pageNum == pageResult.currentPage ? 'active' : ''}">
                                    <a class="page-link ${pageNum == pageResult.currentPage ? 'bg-success border-success text-white' : 'text-success'}"
                                       href="${pageContext.request.contextPath}/gardener/projects?page=${pageNum}&status=${selectedStatus}">${pageNum}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${!pageResult.hasNext() ? 'disabled' : ''}">
                                <a class="page-link text-success"
                                   href="${pageContext.request.contextPath}/gardener/projects?page=${pageResult.nextPage}&status=${selectedStatus}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:when>
            <c:otherwise>
                <div class="card card-botanical text-center p-5 border-0 shadow-sm my-4">
                    <i class="bi bi-kanban display-4 text-muted mb-3"></i>
                    <h4 class="fw-bold text-dark">No Gardening Projects Found</h4>
                    <p class="text-muted mx-auto mb-4" style="max-width: 500px;">
                        Track your crop varieties, planting schedules, compost builds, or greenhouse projects from seed to harvest.
                    </p>
                    <div>
                        <a href="${pageContext.request.contextPath}/gardener/projects/create" class="btn btn-botanical">
                            Start a Project Now
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
