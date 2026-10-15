<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container py-5 text-center my-auto">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
            <div class="card card-botanical p-5 border-0 shadow-sm">
                <div class="d-inline-flex align-items-center justify-content-center bg-danger bg-opacity-10 text-danger rounded-circle p-4 mx-auto mb-3">
                    <i class="bi bi-shield-lock-fill display-3"></i>
                </div>
                <h1 class="display-5 fw-bold text-dark mb-2">403 Forbidden</h1>
                <h4 class="h5 text-muted mb-3">Access Denied</h4>
                <p class="text-muted mb-4">
                    <c:out value="${errorMessage != null ? errorMessage : 'You do not have permission to access the requested resource or perform this action.'}" />
                </p>
                <div class="d-flex justify-content-center gap-3">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-botanical">
                        <i class="bi bi-house me-1"></i> Return Home
                    </a>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary">
                        <i class="bi bi-person me-1"></i> Switch Account
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
