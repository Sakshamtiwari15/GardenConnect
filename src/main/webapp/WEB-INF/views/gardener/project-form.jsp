<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/fragments/gardener-sidebar.jsp" />

    <main class="dashboard-content">
        <div class="mb-4">
            <h1 class="h3 fw-bold text-dark mb-1">
                <c:choose>
                    <c:when test="${not empty project}">Edit Gardening Project</c:when>
                    <c:otherwise>Start a New Gardening Project</c:otherwise>
                </c:choose>
            </h1>
            <p class="text-muted mb-0">Set your timeline, plant variety, and track development milestones.</p>
        </div>

        <div class="row g-4">
            <div class="col-lg-8">
                <div class="card card-botanical p-4 border-0 shadow-sm">
                    <form action="${pageContext.request.contextPath}/gardener/projects/${not empty project ? 'edit' : 'create'}" method="post">
                        <input type="hidden" name="_csrf" value="${csrfToken}" />
                        <c:if test="${not empty project}">
                            <input type="hidden" name="id" value="${project.id}" />
                        </c:if>

                        <div class="mb-3">
                            <label for="title" class="form-label fw-semibold">Project Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control ${not empty fieldErrors.title ? 'is-invalid' : ''}"
                                   id="title" name="title" required
                                   value="<c:out value='${not empty project ? project.title : title}' />"
                                   placeholder="e.g. Raised Bed Heirloom Tomatoes & Companion Basil">
                            <c:if test="${not empty fieldErrors.title}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors.title}" /></div>
                            </c:if>
                        </div>

                        <div class="mb-3">
                            <label for="plantCropType" class="form-label fw-semibold">Plant / Crop / Project Type</label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="bi bi-tag text-muted"></i></span>
                                <input type="text" class="form-control" id="plantCropType" name="plantCropType"
                                       value="<c:out value='${not empty project ? project.plantCropType : plantCropType}' />"
                                       placeholder="e.g. Cherokee Purple Tomatoes, Vertical Herbs, Compost Bin">
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-sm-6">
                                <label for="startDate" class="form-label fw-semibold">Start Date</label>
                                <input type="date" class="form-control" id="startDate" name="startDate"
                                       value="<fmt:formatDate value='${project.startDate}' pattern='yyyy-MM-dd' />">
                            </div>
                            <div class="col-sm-6">
                                <label for="targetCompletionDate" class="form-label fw-semibold">Expected Completion / Harvest Date</label>
                                <input type="date" class="form-control ${not empty fieldErrors.targetCompletionDate ? 'is-invalid' : ''}"
                                       id="targetCompletionDate" name="targetCompletionDate"
                                       value="<fmt:formatDate value='${project.targetCompletionDate}' pattern='yyyy-MM-dd' />">
                                <c:if test="${not empty fieldErrors.targetCompletionDate}">
                                    <div class="invalid-feedback"><c:out value="${fieldErrors.targetCompletionDate}" /></div>
                                </c:if>
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-sm-6">
                                <label for="status" class="form-label fw-semibold">Project Status</label>
                                <select class="form-select" id="status" name="status">
                                    <c:forEach var="st" items="${statuses}">
                                        <option value="${st.name()}" ${project.status == st ? 'selected' : ''}>
                                            <c:out value="${st.displayName}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-sm-6">
                                <label for="progressPercentage" class="form-label fw-semibold d-flex justify-content-between">
                                    <span>Progress</span>
                                    <span id="progressDisplay" class="fw-bold text-success">${not empty project ? project.progressPercentage : 0}%</span>
                                </label>
                                <input type="range" class="form-range" id="progressPercentage" name="progressPercentage"
                                       min="0" max="100" value="${not empty project ? project.progressPercentage : 0}">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label fw-semibold">Project Scope & Objectives</label>
                            <textarea class="form-control" id="description" name="description" rows="3"
                                      placeholder="What are your goals for this crop? (e.g. soil amendments, expected yield, pest strategies)"><c:out value="${not empty project ? project.description : description}" /></textarea>
                        </div>

                        <div class="mb-4">
                            <label for="notes" class="form-label fw-semibold">Cultivation Notes & Observations</label>
                            <textarea class="form-control" id="notes" name="notes" rows="3"
                                      placeholder="Log germination dates, first fruit set, weather events, or feeding schedules..."><c:out value="${not empty project ? project.notes : notes}" /></textarea>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <a href="${pageContext.request.contextPath}/gardener/projects" class="btn btn-outline-secondary">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-botanical px-4">
                                <i class="bi bi-save me-1"></i>
                                <c:choose>
                                    <c:when test="${not empty project}">Save Project Changes</c:when>
                                    <c:otherwise>Create Gardening Project</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Helpful Project Tips Sidebar -->
            <div class="col-lg-4">
                <div class="card card-botanical p-4 border-0 shadow-sm mb-4">
                    <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                        <i class="bi bi-lightbulb text-success"></i> Project Best Practices
                    </h5>
                    <ul class="small text-muted ps-3 mb-0">
                        <li class="mb-2"><strong>Record Variety Names:</strong> Note specific cultivars (e.g. <em>Cherokee Purple</em> vs generic tomato) to assess flavor and disease resistance.</li>
                        <li class="mb-2"><strong>Track Milestones:</strong> Update your progress slider (0 to 100%) as your crops transition from seedling to flowering to fruiting.</li>
                        <li class="mb-2"><strong>Automatic Completion:</strong> Setting progress to 100% automatically switches project status to <em>Completed</em>.</li>
                        <li><strong>Isolation:</strong> Personal projects are private to your gardener profile.</li>
                    </ul>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
