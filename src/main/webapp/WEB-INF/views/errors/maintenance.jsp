<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<div class="container py-5 text-center my-auto">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
            <div class="card card-botanical p-5 border-0 shadow-sm">
                <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle p-4 mx-auto mb-3">
                    <i class="bi bi-tools display-3"></i>
                </div>
                <h1 class="display-5 fw-bold text-dark mb-2">Platform Maintenance</h1>
                <h4 class="h5 text-muted mb-3">Tending to the Garden Beds</h4>
                <p class="text-muted mb-4">
                    GardenConnect is currently undergoing scheduled soil rejuvenation and database maintenance. We will be back online shortly!
                </p>
                <div class="small text-muted mb-4">
                    Platform Administrator? <a href="${pageContext.request.contextPath}/login" class="text-success fw-bold">Sign In to Admin Portal</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
