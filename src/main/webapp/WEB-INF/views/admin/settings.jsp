<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="dashboard-layout">
    <!-- Admin Sidebar -->
    <jsp:include page="/WEB-INF/views/fragments/admin-sidebar.jsp" />

    <!-- Admin Main Content -->
    <main class="dashboard-content">
        <!-- Top Bar Header -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
            <div>
                <span class="badge bg-danger px-3 py-1 rounded-pill fw-bold mb-1">Configuration</span>
                <h1 class="h3 fw-bold text-dark mb-0">System Settings & Policies</h1>
            </div>
            <div class="mt-3 mt-md-0">
                <span class="text-muted small">
                    <i class="bi bi-database-check text-success me-1"></i> Persisted to MySQL <code>system_settings</code>
                </span>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/admin/settings" method="post">
            <input type="hidden" name="_csrf" value="${csrfToken}" />

            <div class="row g-4 mb-4">
                <!-- General Community Configuration -->
                <div class="col-lg-6">
                    <div class="card card-botanical border-0 shadow-sm h-100 p-4">
                        <div class="d-flex align-items-center gap-2 mb-3">
                            <div class="bg-success bg-opacity-10 text-success p-2 rounded-circle">
                                <i class="bi bi-flower1 fs-5"></i>
                            </div>
                            <div>
                                <h5 class="fw-bold text-dark mb-0">Platform Identity</h5>
                                <small class="text-muted">Branding, titles, and public communication</small>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="community_name" class="form-label fw-semibold">Community Platform Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="community_name" name="community_name" value="<c:out value='${not empty settings["community_name"] ? settings["community_name"].value : "GardenConnect"}' />" required />
                            <small class="text-muted">Displayed in navigation bars, page titles, and hero headers.</small>
                        </div>

                        <div class="mb-3">
                            <label for="community_description" class="form-label fw-semibold">Platform Tagline & Description</label>
                            <textarea class="form-control" id="community_description" name="community_description" rows="3"><c:out value='${not empty settings["community_description"] ? settings["community_description"].value : "Cultivating Knowledge, Growing Together."}' /></textarea>
                            <small class="text-muted">Brief overview shown on the landing page and community meta tags.</small>
                        </div>

                        <div class="mb-3">
                            <label for="contact_email" class="form-label fw-semibold">Support & Administrator Contact Email</label>
                            <input type="email" class="form-control" id="contact_email" name="contact_email" value="<c:out value='${not empty settings["contact_email"] ? settings["contact_email"].value : "support@gardenconnect.org"}' />" />
                            <small class="text-muted">Public contact address for inquiries and account assistance.</small>
                        </div>
                    </div>
                </div>

                <!-- Access & Moderation Policies -->
                <div class="col-lg-6">
                    <div class="card card-botanical border-0 shadow-sm h-100 p-4">
                        <div class="d-flex align-items-center gap-2 mb-3">
                            <div class="bg-warning bg-opacity-10 text-warning p-2 rounded-circle">
                                <i class="bi bi-shield-lock-fill fs-5"></i>
                            </div>
                            <div>
                                <h5 class="fw-bold text-dark mb-0">Access & Moderation Policies</h5>
                                <small class="text-muted">Control registration, maintenance, and approval rules</small>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="moderation_policy" class="form-label fw-semibold">Content Moderation Policy</label>
                            <select class="form-select" id="moderation_policy" name="moderation_policy">
                                <c:set var="currentPolicy" value="${not empty settings['moderation_policy'] ? settings['moderation_policy'].value : 'STANDARD'}" />
                                <option value="STRICT" ${currentPolicy == 'STRICT' ? 'selected' : ''}>Strict (All tips and discussions require explicit admin approval)</option>
                                <option value="STANDARD" ${currentPolicy == 'STANDARD' ? 'selected' : ''}>Standard (Tips require approval; trusted users can post directly)</option>
                                <option value="LAX" ${currentPolicy == 'LAX' ? 'selected' : ''}>Lax (Immediate publish with retroactive report moderation)</option>
                            </select>
                            <small class="text-muted">Determines whether newly submitted tips enter the PENDING review queue.</small>
                        </div>

                        <div class="mb-3">
                            <label for="registration_open" class="form-label fw-semibold">Member Registration Status</label>
                            <select class="form-select" id="registration_open" name="registration_open">
                                <c:set var="regOpen" value="${not empty settings['registration_open'] ? settings['registration_open'].value : 'true'}" />
                                <option value="true" ${regOpen == 'true' ? 'selected' : ''}>Open (Public visitors can create new gardener accounts)</option>
                                <option value="false" ${regOpen == 'false' ? 'selected' : ''}>Closed / Invite-Only (Only admins can provision accounts)</option>
                            </select>
                            <small class="text-muted">Toggle whether new gardeners can register via <code>/register</code>.</small>
                        </div>

                        <div class="mb-3">
                            <label for="maintenance_mode" class="form-label fw-semibold text-danger">Platform Maintenance Mode</label>
                            <select class="form-select border-warning" id="maintenance_mode" name="maintenance_mode">
                                <c:set var="mMode" value="${not empty settings['maintenance_mode'] ? settings['maintenance_mode'].value : 'false'}" />
                                <option value="false" ${mMode == 'false' ? 'selected' : ''}>Normal Operation (Site accessible to all users)</option>
                                <option value="true" ${mMode == 'true' ? 'selected' : ''}>Maintenance Mode (Non-admins redirected to maintenance page)</option>
                            </select>
                            <small class="text-danger">
                                <i class="bi bi-exclamation-triangle me-1"></i> Administrators retain full access during maintenance.
                            </small>
                        </div>
                    </div>
                </div>

                <!-- Numerical Operational Limits -->
                <div class="col-12">
                    <div class="card card-botanical border-0 shadow-sm p-4">
                        <div class="d-flex align-items-center gap-2 mb-3">
                            <div class="bg-info bg-opacity-10 text-info p-2 rounded-circle">
                                <i class="bi bi-sliders fs-5"></i>
                            </div>
                            <div>
                                <h5 class="fw-bold text-dark mb-0">System Limits & Pagination</h5>
                                <small class="text-muted">File upload constraints and feed pagination defaults</small>
                            </div>
                        </div>

                        <div class="row g-3">
                            <div class="col-md-6">
                                <label for="max_upload_mb" class="form-label fw-semibold">Max Upload Size (MB)</label>
                                <input type="number" class="form-control" id="max_upload_mb" name="max_upload_mb" min="1" max="50" value="<c:out value='${not empty settings["max_upload_mb"] ? settings["max_upload_mb"].value : "5"}' />" required />
                                <small class="text-muted">Maximum allowed file size in Megabytes for attached plant photos.</small>
                            </div>
                            <div class="col-md-6">
                                <label for="default_page_size" class="form-label fw-semibold">Default Pagination Size</label>
                                <input type="number" class="form-control" id="default_page_size" name="default_page_size" min="5" max="100" value="<c:out value='${not empty settings["default_page_size"] ? settings["default_page_size"].value : "10"}' />" required />
                                <small class="text-muted">Number of cards/items rendered per page in feeds and lists.</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Action Buttons Bar -->
            <div class="d-flex justify-content-between align-items-center p-3 bg-white rounded shadow-sm">
                <div class="text-muted small">
                    <i class="bi bi-shield-check text-success me-1"></i> Changes are audited in the activity log with your admin identity.
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">Cancel</a>
                    <button type="submit" class="btn btn-botanical px-4">
                        <i class="bi bi-save2 me-1"></i> Save Configuration
                    </button>
                </div>
            </div>
        </form>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
