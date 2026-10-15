<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />
<jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

<div class="container py-5">
    <!-- Breadcrumbs -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/discussions">Discussions</a></li>
            <li class="breadcrumb-item active" aria-current="page"><c:out value="${discussion.title}" /></li>
        </ol>
    </nav>

    <div class="row g-4">
        <!-- Main Thread & Comments Column -->
        <div class="col-lg-8">
            <!-- Main Thread Post -->
            <div class="card card-botanical p-4 p-md-5 border-0 shadow-sm mb-4">
                <div class="d-flex align-items-center gap-2 mb-3">
                    <span class="badge badge-botanical"><c:out value="${discussion.categoryName}" /></span>
                    <span class="text-muted small ms-auto"><i class="bi bi-eye me-1"></i> ${discussion.viewCount} Views</span>
                </div>

                <h1 class="h3 fw-bold text-dark mb-3"><c:out value="${discussion.title}" /></h1>

                <div class="d-flex align-items-center gap-3 pb-3 mb-4 border-bottom">
                    <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle p-2">
                        <i class="bi bi-person-fill fs-4"></i>
                    </div>
                    <div>
                        <div class="fw-bold text-dark"><c:out value="${discussion.authorName}" /></div>
                        <small class="text-muted">@<c:out value="${discussion.authorUsername}" /> &bull; <fmt:formatDate value="${discussion.createdAt}" pattern="MMM dd, yyyy HH:mm" /></small>
                    </div>

                    <!-- Owner / Admin Delete Button -->
                    <c:if test="${not empty sessionScope.currentUser and (sessionScope.currentUser.id == discussion.userId or sessionScope.currentUser.admin)}">
                        <form action="${pageContext.request.contextPath}/gardener/discussions/delete" method="post" class="ms-auto">
                            <input type="hidden" name="_csrf" value="${csrfToken}" />
                            <input type="hidden" name="id" value="${discussion.id}" />
                            <button type="submit" class="btn btn-sm btn-outline-danger" data-confirm="Delete this entire discussion thread?">
                                <i class="bi bi-trash me-1"></i> Delete Topic
                            </button>
                        </form>
                    </c:if>
                </div>

                <div class="discussion-content fs-5 lh-lg" style="white-space: pre-line;">
                    <c:out value="${discussion.content}" />
                </div>
            </div>

            <!-- Comments Stream Section -->
            <div class="card card-botanical p-4 border-0 shadow-sm mb-4">
                <h4 class="h5 fw-bold mb-4 d-flex align-items-center gap-2">
                    <i class="bi bi-chat-left-text text-success"></i>
                    Replies &bull; <span>${comments.size()}</span>
                </h4>

                <c:choose>
                    <c:when test="${not empty comments}">
                        <div class="d-flex flex-column gap-3 mb-4">
                            <c:forEach var="comment" items="${comments}">
                                <div class="p-3 bg-light rounded-3 border">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <strong class="text-dark"><c:out value="${comment.authorName}" /></strong>
                                            <small class="text-muted ms-2">@<c:out value="${comment.authorUsername}" /></small>
                                            <c:if test="${not empty comment.authorLocation}">
                                                <small class="text-muted ms-2"><i class="bi bi-geo-alt"></i> <c:out value="${comment.authorLocation}" /></small>
                                            </c:if>
                                        </div>
                                        <div class="d-flex align-items-center gap-2">
                                            <small class="text-muted"><fmt:formatDate value="${comment.createdAt}" pattern="MMM dd, yyyy HH:mm" /></small>
                                            <c:if test="${not empty sessionScope.currentUser and (sessionScope.currentUser.id == comment.userId or sessionScope.currentUser.admin)}">
                                                <form action="${pageContext.request.contextPath}/gardener/comments/delete" method="post" class="d-inline">
                                                    <input type="hidden" name="_csrf" value="${csrfToken}" />
                                                    <input type="hidden" name="commentId" value="${comment.id}" />
                                                    <input type="hidden" name="discussionId" value="${discussion.id}" />
                                                    <button type="submit" class="btn btn-link text-danger p-0 ms-2" title="Delete comment" data-confirm="Delete this comment?">
                                                        <i class="bi bi-x-circle"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>
                                    <p class="mb-0 text-dark" style="white-space: pre-line;"><c:out value="${comment.content}" /></p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-4 text-muted">
                            <i class="bi bi-chat-dots fs-1 mb-2 d-inline-block"></i>
                            <p>No replies on this topic yet. Be the first to chime in!</p>
                        </div>
                    </c:otherwise>
                </c:choose>

                <!-- Post Comment Form -->
                <div class="pt-3 border-top">
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <form action="${pageContext.request.contextPath}/gardener/comments/create" method="post">
                                <input type="hidden" name="_csrf" value="${csrfToken}" />
                                <input type="hidden" name="discussionId" value="${discussion.id}" />
                                <div class="mb-3">
                                    <label for="commentContent" class="form-label fw-semibold">Add Your Reply</label>
                                    <textarea class="form-control" id="commentContent" name="content" rows="3" required
                                              placeholder="Share advice, ask follow-up questions, or provide plant experiences..."></textarea>
                                </div>
                                <div class="text-end">
                                    <button type="submit" class="btn btn-botanical">
                                        <i class="bi bi-send me-1"></i> Post Reply
                                    </button>
                                </div>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="p-3 bg-light rounded text-center">
                                <p class="mb-2 text-muted">Want to join this discussion?</p>
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-botanical btn-sm">
                                    <i class="bi bi-box-arrow-in-right me-1"></i> Sign In to Reply
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Back to discussions feed -->
            <a href="${pageContext.request.contextPath}/discussions" class="btn btn-botanical-outline">
                <i class="bi bi-arrow-left me-1"></i> Back to Discussions
            </a>
        </div>

        <!-- Sidebar Column -->
        <div class="col-lg-4">
            <div class="card card-botanical p-4 border-0 shadow-sm mb-4">
                <h5 class="fw-bold text-dark mb-3">Community Guidelines</h5>
                <ul class="small text-muted ps-3 mb-0">
                    <li class="mb-2">Be kind, respectful, and constructive in all discussions.</li>
                    <li class="mb-2">Share specific details (light, soil, zone, watering) when diagnosing plant problems.</li>
                    <li class="mb-2">Celebrate experimentations and failures as learning opportunities.</li>
                    <li>No commercial spam or unauthorized product promotions.</li>
                </ul>
            </div>

            <div class="card card-botanical p-4 border-0 shadow-sm">
                <h5 class="fw-bold text-dark mb-3">Browse by Category</h5>
                <div class="d-flex flex-column gap-2">
                    <c:forEach var="cat" items="${categories}">
                        <a href="${pageContext.request.contextPath}/discussions?category=${cat.id}"
                           class="d-flex justify-content-between align-items-center text-dark text-decoration-none py-1 border-bottom last-border-0">
                            <span><i class="bi ${cat.icon} text-success me-2"></i> <c:out value="${cat.name}" /></span>
                            <i class="bi bi-chevron-right text-muted small"></i>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
