<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="container py-5">
    <div class="row g-5">
        <!-- Main Content Column -->
        <div class="col-lg-8">
            <!-- Breadcrumbs -->
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/tips">Tips</a></li>
                    <li class="breadcrumb-item active" aria-current="page"><c:out value="${tip.title}" /></li>
                </ol>
            </nav>

            <article class="card card-botanical p-4 p-md-5 border-0 shadow-sm">
                <!-- Header Meta -->
                <div class="d-flex flex-wrap align-items-center gap-2 mb-3">
                    <span class="badge badge-botanical fs-6"><c:out value="${tip.categoryName}" /></span>
                    <c:if test="${!tip.approved}">
                        <span class="badge bg-warning text-dark"><i class="bi bi-clock me-1"></i> Under Moderation Review</span>
                    </c:if>
                    <span class="text-muted small ms-auto"><i class="bi bi-eye me-1"></i> ${tip.viewCount} Views</span>
                </div>

                <h1 class="display-6 fw-bold text-dark mb-3"><c:out value="${tip.title}" /></h1>

                <!-- Author & Date Byline -->
                <div class="d-flex align-items-center gap-3 pb-3 mb-4 border-bottom">
                    <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle p-2">
                        <i class="bi bi-person-fill fs-4"></i>
                    </div>
                    <div>
                        <div class="fw-bold text-dark"><c:out value="${tip.authorName}" /></div>
                        <small class="text-muted">@<c:out value="${tip.authorUsername}" /> &bull; Published on <fmt:formatDate value="${tip.createdAt}" pattern="MMM dd, yyyy" /></small>
                    </div>

                    <!-- Owner/Admin Action Buttons -->
                    <c:if test="${not empty sessionScope.currentUser and (sessionScope.currentUser.id == tip.userId or sessionScope.currentUser.admin)}">
                        <div class="ms-auto d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/gardener/tips/edit?id=${tip.id}" class="btn btn-sm btn-outline-secondary">
                                <i class="bi bi-pencil me-1"></i> Edit
                            </a>
                            <form action="${pageContext.request.contextPath}/gardener/tips/delete" method="post" class="d-inline">
                                <input type="hidden" name="_csrf" value="${csrfToken}" />
                                <input type="hidden" name="id" value="${tip.id}" />
                                <button type="submit" class="btn btn-sm btn-outline-danger" data-confirm="Are you sure you want to delete this gardening tip?">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </div>
                    </c:if>
                </div>

                <!-- Featured Image -->
                <c:if test="${not empty tip.imageUrl}">
                    <div class="mb-4 rounded-3 overflow-hidden shadow-sm">
                        <img src="${tip.imageUrl}" class="img-fluid w-100" alt="${tip.title}" style="max-height: 450px; object-fit: cover;">
                    </div>
                </c:if>

                <!-- Tip Summary Lead -->
                <c:if test="${not empty tip.summary}">
                    <div class="lead text-dark bg-light p-3 rounded-3 mb-4 border-start border-4 border-success">
                        <c:out value="${tip.summary}" />
                    </div>
                </c:if>

                <!-- Full Content Body -->
                <div class="tip-content fs-5 lh-lg" style="white-space: pre-line;">
                    <c:out value="${tip.content}" />
                </div>
            </article>

            <!-- Back to tips feed -->
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/tips" class="btn btn-botanical-outline">
                    <i class="bi bi-arrow-left me-1"></i> Back to All Gardening Tips
                </a>
            </div>
        </div>

        <!-- Sidebar Recommendations Column -->
        <div class="col-lg-4">
            <div class="card card-botanical p-4 border-0 shadow-sm mb-4">
                <h5 class="fw-bold text-dark mb-3">Related Guides</h5>
                <c:forEach var="relTip" items="${recentTips}">
                    <div class="mb-3 pb-3 border-bottom last-border-0">
                        <span class="badge badge-botanical mb-1"><c:out value="${relTip.categoryName}" /></span>
                        <h6 class="fw-bold mb-1">
                            <a href="${pageContext.request.contextPath}/tips/view?id=${relTip.id}" class="text-dark text-decoration-none">
                                <c:out value="${relTip.title}" />
                            </a>
                        </h6>
                        <small class="text-muted"><i class="bi bi-eye me-1"></i>${relTip.viewCount} views</small>
                    </div>
                </c:forEach>
            </div>

            <!-- Share Wisdom CTA -->
            <div class="card card-botanical p-4 border-0 shadow-sm bg-success bg-opacity-10 text-center">
                <i class="bi bi-lightbulb fs-1 text-success mb-2"></i>
                <h5 class="fw-bold text-dark">Have Gardening Knowledge to Share?</h5>
                <p class="small text-muted mb-3">Join our network of growers and help community members succeed in their plots.</p>
                <a href="${pageContext.request.contextPath}/gardener/tips/create" class="btn btn-botanical btn-sm">
                    Submit a Guide
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
