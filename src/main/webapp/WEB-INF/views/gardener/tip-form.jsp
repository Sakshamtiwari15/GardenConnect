<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/fragments/gardener-sidebar.jsp" />

    <main class="dashboard-content">
        <div class="mb-4">
            <h1 class="h3 fw-bold text-dark mb-1">
                <c:choose>
                    <c:when test="${not empty tip}">Edit Gardening Tip</c:when>
                    <c:otherwise>Share a New Gardening Tip</c:otherwise>
                </c:choose>
            </h1>
            <p class="text-muted mb-0">Help other community gardeners grow healthier plants with proven advice.</p>
        </div>

        <div class="row g-4">
            <div class="col-lg-8">
                <div class="card card-botanical p-4 border-0 shadow-sm">
                    <form action="${pageContext.request.contextPath}/gardener/tips/${not empty tip ? 'edit' : 'create'}" method="post">
                        <input type="hidden" name="_csrf" value="${csrfToken}" />
                        <c:if test="${not empty tip}">
                            <input type="hidden" name="id" value="${tip.id}" />
                        </c:if>

                        <div class="mb-3">
                            <label for="categoryId" class="form-label fw-semibold">Category <span class="text-danger">*</span></label>
                            <select class="form-select ${not empty fieldErrors.categoryId ? 'is-invalid' : ''}" id="categoryId" name="categoryId" required>
                                <option value="" disabled ${empty tip and empty selectedCategoryId ? 'selected' : ''}>Select a category...</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.id}" ${(tip.categoryId == cat.id or selectedCategoryId == cat.id) ? 'selected' : ''}>
                                        <c:out value="${cat.name}" />
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty fieldErrors.categoryId}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors.categoryId}" /></div>
                            </c:if>
                        </div>

                        <div class="mb-3">
                            <label for="title" class="form-label fw-semibold">Tip Title <span class="text-danger">*</span></label>
                            <input type="text" class="form-control ${not empty fieldErrors.title ? 'is-invalid' : ''}"
                                   id="title" name="title" required
                                   value="<c:out value='${not empty tip ? tip.title : title}' />"
                                   placeholder="e.g. Preventing Blossom End Rot in Heirlooms with Crushed Eggshells">
                            <c:if test="${not empty fieldErrors.title}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors.title}" /></div>
                            </c:if>
                            <div class="form-text small">Between 5 and 200 characters.</div>
                        </div>

                        <div class="mb-3">
                            <label for="summary" class="form-label fw-semibold">Lead Summary</label>
                            <input type="text" class="form-control" id="summary" name="summary"
                                   value="<c:out value='${not empty tip ? tip.summary : summary}' />"
                                   placeholder="A brief 1-2 sentence overview of what gardeners will learn...">
                        </div>

                        <div class="mb-3">
                            <label for="imageUrl" class="form-label fw-semibold">Photo / Illustration URL (Optional)</label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="bi bi-image text-muted"></i></span>
                                <input type="url" class="form-control" id="imageUrl" name="imageUrl"
                                       value="<c:out value='${not empty tip ? tip.imageUrl : imageUrl}' />"
                                       placeholder="https://images.unsplash.com/photo-...">
                            </div>
                            <div class="form-text small">Direct HTTPS URL to a photo of your plants, compost, or setup.</div>
                        </div>

                        <div class="mb-4">
                            <label for="content" class="form-label fw-semibold">Full Technique / Guide Content <span class="text-danger">*</span></label>
                            <textarea class="form-control ${not empty fieldErrors.content ? 'is-invalid' : ''}"
                                      id="content" name="content" rows="10" required
                                      placeholder="Detail step-by-step instructions, materials needed, seasonal timing, common mistakes, and expected results..."><c:out value="${not empty tip ? tip.content : content}" /></textarea>
                            <c:if test="${not empty fieldErrors.content}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors.content}" /></div>
                            </c:if>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <a href="${pageContext.request.contextPath}/gardener/tips" class="btn btn-outline-secondary">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-botanical px-4">
                                <i class="bi bi-send me-1"></i>
                                <c:choose>
                                    <c:when test="${not empty tip}">Update & Resubmit</c:when>
                                    <c:otherwise>Submit Tip for Review</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Guidelines Callout Sidebar -->
            <div class="col-lg-4">
                <div class="card card-botanical p-4 border-0 shadow-sm mb-4">
                    <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                        <i class="bi bi-shield-check text-success"></i> Community Quality Standards
                    </h5>
                    <p class="small text-muted">
                        To maintain high quality across GardenConnect, all submitted guides undergo moderator review before appearing in public feeds.
                    </p>
                    <ul class="small text-muted ps-3 mb-0">
                        <li class="mb-2"><strong>First-Hand Experience:</strong> Share techniques you have personally tested.</li>
                        <li class="mb-2"><strong>Clarity:</strong> Include measurements, temperature ranges, or timing where applicable.</li>
                        <li class="mb-2"><strong>Editing:</strong> If you edit a published tip, it briefly re-enters the moderation queue.</li>
                        <li><strong>Respect:</strong> No spam or unverified chemical advice.</li>
                    </ul>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
