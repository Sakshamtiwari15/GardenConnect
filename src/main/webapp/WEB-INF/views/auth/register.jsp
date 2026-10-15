<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
            <div class="card card-botanical shadow-md p-4 p-md-5 border-0">
                <div class="text-center mb-4">
                    <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle p-3 mb-3">
                        <i class="bi bi-seedling fs-2"></i>
                    </div>
                    <h2 class="fw-bold text-dark">Join GardenConnect</h2>
                    <p class="text-muted">Create your free gardener account today</p>
                </div>

                <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post">
                    <!-- CSRF Protection -->
                    <input type="hidden" name="_csrf" value="${csrfToken}" />

                    <div class="mb-3">
                        <label for="fullName" class="form-label fw-semibold">Full Name</label>
                        <input type="text" class="form-control ${not empty fieldErrors.fullName ? 'is-invalid' : ''}"
                               id="fullName" name="fullName" value="<c:out value='${fullName}' />" required placeholder="e.g. Jane Appleseed">
                        <c:if test="${not empty fieldErrors.fullName}">
                            <div class="invalid-feedback"><c:out value="${fieldErrors.fullName}" /></div>
                        </c:if>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-sm-6">
                            <label for="username" class="form-label fw-semibold">Username</label>
                            <input type="text" class="form-control ${not empty fieldErrors.username ? 'is-invalid' : ''}"
                                   id="username" name="username" value="<c:out value='${username}' />" required placeholder="jane_gardens">
                            <c:if test="${not empty fieldErrors.username}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors.username}" /></div>
                            </c:if>
                        </div>
                        <div class="col-sm-6">
                            <label for="email" class="form-label fw-semibold">Email Address</label>
                            <input type="email" class="form-control ${not empty fieldErrors.email ? 'is-invalid' : ''}"
                                   id="email" name="email" value="<c:out value='${email}' />" required placeholder="jane@example.com">
                            <c:if test="${not empty fieldErrors.email}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors.email}" /></div>
                            </c:if>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label fw-semibold">Password</label>
                        <input type="password" class="form-control ${not empty fieldErrors.password ? 'is-invalid' : ''}"
                               id="password" name="password" required placeholder="Minimum 8 characters">
                        <c:if test="${not empty fieldErrors.password}">
                            <div class="invalid-feedback"><c:out value="${fieldErrors.password}" /></div>
                        </c:if>
                        <div class="form-text small">
                            Must be 8+ characters and contain uppercase, lowercase, number, and special symbol.
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label fw-semibold">Confirm Password</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               required placeholder="Re-enter your password">
                    </div>

                    <button type="submit" class="btn btn-botanical w-100 py-2 fs-6">
                        <i class="bi bi-person-check me-1"></i> Create Free Account
                    </button>
                </form>

                <div class="text-center mt-4 pt-3 border-top">
                    <p class="text-muted mb-0">Already a community member?
                        <a href="${pageContext.request.contextPath}/login" class="fw-bold text-success text-decoration-none">
                            Sign In Here
                        </a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
