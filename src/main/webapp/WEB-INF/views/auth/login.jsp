<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card card-botanical shadow-md p-4 p-md-5 border-0">
                <div class="text-center mb-4">
                    <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle p-3 mb-3">
                        <i class="bi bi-person-lock fs-2"></i>
                    </div>
                    <h2 class="fw-bold text-dark">Welcome Back</h2>
                    <p class="text-muted">Sign in to your GardenConnect account</p>
                </div>

                <form action="${pageContext.request.contextPath}/login" method="post">
                    <!-- CSRF Protection -->
                    <input type="hidden" name="_csrf" value="${csrfToken}" />
                    <c:if test="${not empty returnUrl}">
                        <input type="hidden" name="returnUrl" value="<c:out value='${returnUrl}' />" />
                    </c:if>

                    <div class="mb-3">
                        <label for="usernameOrEmail" class="form-label fw-semibold">Username or Email</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0"><i class="bi bi-person text-muted"></i></span>
                            <input type="text" class="form-control border-start-0" id="usernameOrEmail" name="usernameOrEmail"
                                   value="<c:out value='${usernameOrEmail}' />" required autofocus placeholder="e.g. sarah_green or admin">
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="password" class="form-label fw-semibold">Password</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0"><i class="bi bi-key text-muted"></i></span>
                            <input type="password" class="form-control border-start-0" id="password" name="password"
                                   required placeholder="Enter your password">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-botanical w-100 py-2 fs-6">
                        <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                    </button>
                </form>

                <div class="text-center mt-4 pt-3 border-top">
                    <p class="text-muted mb-0">Don't have an account yet?
                        <a href="${pageContext.request.contextPath}/register" class="fw-bold text-success text-decoration-none">
                            Join GardenConnect
                        </a>
                    </p>
                </div>

                <!-- Academic Evaluation Demo Credentials Hint Box -->
                <div class="mt-4 p-3 bg-light rounded border small">
                    <div class="fw-bold text-success mb-1 d-flex align-items-center gap-1">
                        <i class="bi bi-info-circle-fill"></i> Evaluator Demo Credentials:
                    </div>
                    <div class="d-flex justify-content-between mb-1">
                        <span><strong>Admin:</strong> <code>admin</code></span>
                        <span>Pass: <code>Admin@123456</code></span>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span><strong>Gardener:</strong> <code>sarah_green</code></span>
                        <span>Pass: <code>Gardener@123</code></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
