<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-expand-lg navbar-botanical sticky-top">
    <div class="container">
        <!-- Brand Logo -->
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">
            <i class="bi bi-flower1"></i>
            <span><c:out value="${communityName != null ? communityName : 'GardenConnect'}" /></span>
        </a>

        <!-- Mobile Toggle Button -->
        <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent"
                aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
            <i class="bi bi-list fs-2 text-dark"></i>
        </button>

        <!-- Navbar Links -->
        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                <li class="nav-item">
                    <a class="nav-link ${pageContext.request.servletPath == '/home' || pageContext.request.servletPath == '' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${pageContext.request.servletPath.startsWith('/tips') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/tips">Gardening Tips</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${pageContext.request.servletPath.startsWith('/discussions') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/discussions">Discussions</a>
                </li>
            </ul>

            <!-- Right-Hand User Navigation -->
            <div class="d-flex align-items-center gap-2">
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <!-- Logged-in User Menu -->
                        <div class="dropdown">
                            <button class="btn btn-botanical-outline dropdown-toggle d-flex align-items-center gap-2"
                                    type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-person-circle fs-5"></i>
                                <span><c:out value="${sessionScope.currentUser.fullName}" /></span>
                                <c:if test="${sessionScope.currentUser.admin}">
                                    <span class="badge bg-danger ms-1">ADMIN</span>
                                </c:if>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0" aria-labelledby="userDropdown">
                                <li>
                                    <h6 class="dropdown-header text-muted">
                                        Signed in as <strong>@<c:out value="${sessionScope.currentUser.username}" /></strong>
                                    </h6>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/gardener/dashboard">
                                        <i class="bi bi-speedometer2 me-2 text-success"></i> Gardener Dashboard
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/gardener/projects">
                                        <i class="bi bi-kanban me-2 text-success"></i> My Projects
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/gardener/tips">
                                        <i class="bi bi-journal-text me-2 text-success"></i> My Tips
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/gardener/profile">
                                        <i class="bi bi-person-gear me-2 text-success"></i> Edit Profile
                                    </a>
                                </li>

                                <!-- Admin Exclusive Link -->
                                <c:if test="${sessionScope.currentUser.admin}">
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item py-2 fw-bold text-danger" href="${pageContext.request.contextPath}/admin/dashboard">
                                            <i class="bi bi-shield-lock me-2"></i> Administration Portal
                                        </a>
                                    </li>
                                </c:if>

                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="${pageContext.request.contextPath}/logout" method="post" class="px-2">
                                        <input type="hidden" name="_csrf" value="${csrfToken}" />
                                        <button type="submit" class="dropdown-item py-2 text-danger rounded">
                                            <i class="bi bi-box-arrow-right me-2"></i> Sign Out
                                        </button>
                                    </form>
                                </li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Guest Navigation -->
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-botanical-outline">
                            <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-botanical">
                            <i class="bi bi-person-plus me-1"></i> Join Free
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
