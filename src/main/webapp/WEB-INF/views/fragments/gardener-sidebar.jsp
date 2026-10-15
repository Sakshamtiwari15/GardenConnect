<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="sidebar-wrapper">
    <div class="text-center pb-3 mb-3 border-bottom">
        <div class="d-inline-flex align-items-center justify-content-center bg-success bg-opacity-10 text-success rounded-circle p-3 mb-2">
            <i class="bi bi-person-bounding-box fs-3"></i>
        </div>
        <h6 class="mb-0 fw-bold"><c:out value="${sessionScope.currentUser.fullName}" /></h6>
        <small class="text-muted">@<c:out value="${sessionScope.currentUser.username}" /></small>
        <div class="mt-1">
            <span class="badge bg-success bg-opacity-25 text-success">Gardener Member</span>
        </div>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/gardener/dashboard"
               class="sidebar-link ${activeTab == 'dashboard' ? 'active' : ''}">
                <i class="bi bi-speedometer2"></i>
                <span>Dashboard</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/gardener/projects"
               class="sidebar-link ${activeTab == 'projects' ? 'active' : ''}">
                <i class="bi bi-kanban"></i>
                <span>My Projects</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/gardener/tips"
               class="sidebar-link ${activeTab == 'tips' ? 'active' : ''}">
                <i class="bi bi-journal-text"></i>
                <span>My Tips</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/gardener/discussions"
               class="sidebar-link ${activeTab == 'discussions' ? 'active' : ''}">
                <i class="bi bi-chat-quote"></i>
                <span>My Discussions</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/gardener/profile"
               class="sidebar-link ${activeTab == 'profile' ? 'active' : ''}">
                <i class="bi bi-person-gear"></i>
                <span>Profile Settings</span>
            </a>
        </li>

        <li class="pt-3 border-top mt-3">
            <a href="${pageContext.request.contextPath}/" class="sidebar-link text-muted">
                <i class="bi bi-arrow-left-circle"></i>
                <span>Public Community</span>
            </a>
        </li>

        <c:if test="${sessionScope.currentUser.admin}">
            <li class="pt-1">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="sidebar-link text-danger">
                    <i class="bi bi-shield-lock"></i>
                    <span>Admin Portal</span>
                </a>
            </li>
        </c:if>

        <li class="pt-2">
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <input type="hidden" name="_csrf" value="${csrfToken}" />
                <button type="submit" class="sidebar-link border-0 bg-transparent w-100 text-start text-danger">
                    <i class="bi bi-box-arrow-right"></i>
                    <span>Sign Out</span>
                </button>
            </form>
        </li>
    </ul>
</div>
