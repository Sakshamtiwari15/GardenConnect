<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container mt-3">
    <!-- Success Alert -->
    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm d-flex align-items-center gap-2" role="alert">
            <i class="bi bi-check-circle-fill fs-5 text-success"></i>
            <div><c:out value="${sessionScope.flashSuccess}" /></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="flashSuccess" scope="session" />
    </c:if>

    <!-- Success Alert from Request -->
    <c:if test="${not empty requestScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm d-flex align-items-center gap-2" role="alert">
            <i class="bi bi-check-circle-fill fs-5 text-success"></i>
            <div><c:out value="${requestScope.successMessage}" /></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Error Alert from Session -->
    <c:if test="${not empty sessionScope.flashError}">
        <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm d-flex align-items-center gap-2" role="alert">
            <i class="bi bi-exclamation-triangle-fill fs-5 text-danger"></i>
            <div><c:out value="${sessionScope.flashError}" /></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="flashError" scope="session" />
    </c:if>

    <!-- Error Alert from Request -->
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm d-flex align-items-center gap-2" role="alert">
            <i class="bi bi-exclamation-octagon-fill fs-5 text-danger"></i>
            <div><c:out value="${requestScope.errorMessage}" /></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Warning Alert -->
    <c:if test="${not empty sessionScope.flashWarning}">
        <div class="alert alert-warning alert-dismissible fade show border-0 shadow-sm d-flex align-items-center gap-2" role="alert">
            <i class="bi bi-exclamation-circle-fill fs-5 text-warning"></i>
            <div><c:out value="${sessionScope.flashWarning}" /></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="flashWarning" scope="session" />
    </c:if>

    <!-- Info Alert -->
    <c:if test="${not empty sessionScope.flashInfo}">
        <div class="alert alert-info alert-dismissible fade show border-0 shadow-sm d-flex align-items-center gap-2" role="alert">
            <i class="bi bi-info-circle-fill fs-5 text-info"></i>
            <div><c:out value="${sessionScope.flashInfo}" /></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="flashInfo" scope="session" />
    </c:if>
</div>
