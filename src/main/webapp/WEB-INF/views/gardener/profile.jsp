<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="dashboard-layout">
    <!-- Gardener Sidebar -->
    <jsp:include page="/WEB-INF/views/fragments/gardener-sidebar.jsp" />

    <!-- Main Content -->
    <main class="dashboard-content">
        <div class="mb-4">
            <h1 class="h3 fw-bold text-dark mb-1">Gardener Profile Settings</h1>
            <p class="text-muted mb-0">Manage your public horticultural persona and account security credentials.</p>
        </div>

        <div class="row g-4">
            <!-- Profile Info Card & Edit Form -->
            <div class="col-lg-7">
                <div class="card card-botanical p-4 border-0 shadow-sm mb-4">
                    <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                        <i class="bi bi-person-lines-fill text-success"></i> Public Profile Details
                    </h5>

                    <form action="${pageContext.request.contextPath}/gardener/profile" method="post">
                        <input type="hidden" name="_csrf" value="${csrfToken}" />

                        <div class="row g-3 mb-3">
                            <div class="col-sm-6">
                                <label class="form-label fw-semibold">Username</label>
                                <input type="text" class="form-control bg-light" value="<c:out value='${user.username}' />" disabled>
                                <div class="form-text small">Usernames cannot be modified.</div>
                            </div>
                            <div class="col-sm-6">
                                <label class="form-label fw-semibold">Email Address</label>
                                <input type="email" class="form-control bg-light" value="<c:out value='${user.email}' />" disabled>
                                <div class="form-text small">Contact support to change email.</div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="fullName" class="form-label fw-semibold">Full Name</label>
                            <input type="text" class="form-control ${not empty fieldErrors.fullName ? 'is-invalid' : ''}"
                                   id="fullName" name="fullName" value="<c:out value='${user.fullName}' />" required>
                            <c:if test="${not empty fieldErrors.fullName}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors.fullName}" /></div>
                            </c:if>
                        </div>

                        <div class="mb-3">
                            <label for="location" class="form-label fw-semibold">Location / Hardiness Zone</label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="bi bi-geo-alt text-muted"></i></span>
                                <input type="text" class="form-control" id="location" name="location"
                                       value="<c:out value='${user.location}' />" placeholder="e.g. Portland, OR &bull; Zone 8b">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="gardeningInterests" class="form-label fw-semibold">Gardening Interests</label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="bi bi-tags text-muted"></i></span>
                                <input type="text" class="form-control" id="gardeningInterests" name="gardeningInterests"
                                       value="<c:out value='${user.gardeningInterests}' />" placeholder="e.g. Heirloom Tomatoes, Vermicomposting, Native Bees">
                            </div>
                            <div class="form-text small">Separate topics with commas to help like-minded growers find you.</div>
                        </div>

                        <div class="mb-4">
                            <label for="bio" class="form-label fw-semibold">Gardener Biography</label>
                            <textarea class="form-control" id="bio" name="bio" rows="4"
                                      placeholder="Share a bit about your garden, growing journey, balcony experiments, or favourite plants..."><c:out value="${user.bio}" /></textarea>
                        </div>

                        <button type="submit" class="btn btn-botanical">
                            <i class="bi bi-save me-1"></i> Save Profile Changes
                        </button>
                    </form>
                </div>
            </div>

            <!-- Password Change & Security Card -->
            <div class="col-lg-5">
                <div class="card card-botanical p-4 border-0 shadow-sm mb-4">
                    <h5 class="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                        <i class="bi bi-shield-lock text-success"></i> Change Password
                    </h5>

                    <form action="${pageContext.request.contextPath}/gardener/profile/password" method="post">
                        <input type="hidden" name="_csrf" value="${csrfToken}" />

                        <div class="mb-3">
                            <label for="currentPassword" class="form-label fw-semibold">Current Password</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                        </div>

                        <div class="mb-3">
                            <label for="newPassword" class="form-label fw-semibold">New Password</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                            <div class="form-text small">Must be 8+ characters with uppercase, lowercase, digit, and special symbol.</div>
                        </div>

                        <div class="mb-4">
                            <label for="confirmPassword" class="form-label fw-semibold">Confirm New Password</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                        </div>

                        <button type="submit" class="btn btn-outline-danger w-100">
                            <i class="bi bi-key me-1"></i> Update Password
                        </button>
                    </form>
                </div>

                <!-- Account Status Overview -->
                <div class="card card-botanical p-4 border-0 shadow-sm">
                    <h6 class="fw-bold text-dark mb-2">Account Overview</h6>
                    <div class="d-flex justify-content-between py-1 border-bottom small">
                        <span class="text-muted">Role</span>
                        <span class="fw-bold"><c:out value="${user.role.displayName}" /></span>
                    </div>
                    <div class="d-flex justify-content-between py-1 border-bottom small">
                        <span class="text-muted">Account Status</span>
                        <span class="badge bg-success">Active</span>
                    </div>
                    <div class="d-flex justify-content-between py-1 small">
                        <span class="text-muted">Member Since</span>
                        <span><fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy" /></span>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
