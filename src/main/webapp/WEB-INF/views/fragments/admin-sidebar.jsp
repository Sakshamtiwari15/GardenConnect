<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="sidebar-wrapper bg-light border-end">
    <div class="text-center pb-3 mb-3 border-bottom">
        <div class="d-inline-flex align-items-center justify-content-center bg-danger bg-opacity-10 text-danger rounded-circle p-3 mb-2">
            <i class="bi bi-shield-shaded fs-3"></i>
        </div>
        <h6 class="mb-0 fw-bold"><c:out value="${sessionScope.currentUser.fullName}" /></h6>
        <small class="text-muted">@<c:out value="${sessionScope.currentUser.username}" /></small>
        <div class="mt-1">
            <span class="badge bg-danger">Platform Administrator</span>
        </div>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/admin/dashboard"
               class="sidebar-link ${activeTab == 'dashboard' ? 'active' : ''}">
                <i class="bi bi-graph-up-arrow"></i>
                <span>Analytics & Overview</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/moderation"
               class="sidebar-link ${activeTab == 'moderation' ? 'active' : ''}">
                <i class="bi bi-shield-check"></i>
                <span>Moderation Queue</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/users"
               class="sidebar-link ${activeTab == 'users' ? 'active' : ''}">
                <i class="bi bi-people"></i>
                <span>User Management</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/settings"
               class="sidebar-link ${activeTab == 'settings' ? 'active' : ''}">
                <i class="bi bi-sliders"></i>
                <span>System Settings</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/activity"
               class="sidebar-link ${activeTab == 'activity' ? 'active' : ''}">
                <i class="bi bi-journal-text"></i>
                <span>Activity Audit Trail</span>
            </a>
        </li>

        <li class="pt-3 border-top mt-3">
            <a href="${pageContext.request.contextPath}/gardener/dashboard" class="sidebar-link text-success">
                <i class="bi bi-person-workspace"></i>
                <span>Gardener Portal</span>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/" class="sidebar-link text-muted">
                <i class="bi bi-house"></i>
                <span>Public Website</span>
            </a>
        </li>

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
