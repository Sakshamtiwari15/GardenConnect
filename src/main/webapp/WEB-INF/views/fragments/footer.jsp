<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<footer class="footer-botanical">
    <div class="container">
        <div class="row g-4">
            <!-- Brand Column -->
            <div class="col-lg-4 col-md-6">
                <h5 class="d-flex align-items-center gap-2">
                    <i class="bi bi-flower1 text-warning"></i>
                    <span><c:out value="${communityName != null ? communityName : 'GardenConnect'}" /></span>
                </h5>
                <p class="text-white-50">
                    A collaborative platform connecting horticulturalists, urban balcony growers, and enthusiastic beginners. Cultivating knowledge, sharing harvests, and celebrating biodiversity.
                </p>
                <div class="d-flex gap-3 fs-5 mt-3">
                    <a href="#" class="text-white-50 hover-white"><i class="bi bi-github"></i></a>
                    <a href="#" class="text-white-50 hover-white"><i class="bi bi-instagram"></i></a>
                    <a href="#" class="text-white-50 hover-white"><i class="bi bi-twitter-x"></i></a>
                    <a href="#" class="text-white-50 hover-white"><i class="bi bi-youtube"></i></a>
                </div>
            </div>

            <!-- Quick Links -->
            <div class="col-lg-2 col-md-6">
                <h5>Community</h5>
                <ul class="list-unstyled d-flex flex-column gap-2">
                    <li><a href="${pageContext.request.contextPath}/tips">Gardening Tips</a></li>
                    <li><a href="${pageContext.request.contextPath}/discussions">Discussion Forums</a></li>
                    <li><a href="${pageContext.request.contextPath}/register">Create Account</a></li>
                    <li><a href="${pageContext.request.contextPath}/login">Member Sign In</a></li>
                </ul>
            </div>

            <!-- Gardening Categories -->
            <div class="col-lg-3 col-md-6">
                <h5>Popular Categories</h5>
                <ul class="list-unstyled d-flex flex-column gap-2 text-white-50">
                    <li><a href="${pageContext.request.contextPath}/tips?category=1">Vegetables & Herbs</a></li>
                    <li><a href="${pageContext.request.contextPath}/tips?category=4">Composting & Soil Health</a></li>
                    <li><a href="${pageContext.request.contextPath}/tips?category=6">Watering & Drip Irrigation</a></li>
                    <li><a href="${pageContext.request.contextPath}/tips?category=8">Organic & Permaculture</a></li>
                </ul>
            </div>

            <!-- Academic Rubric Info -->
            <div class="col-lg-3 col-md-6">
                <h5>Academic Showcase</h5>
                <p class="small text-white-50 mb-2">
                    Developed with <strong>Java 17, Jakarta Servlets 6.0, JSP, JDBC</strong> and <strong>MySQL 8.0</strong> on Apache Tomcat 10.1.
                </p>
                <div class="bg-black bg-opacity-25 p-2 rounded small text-white-50 border border-secondary border-opacity-25">
                    <i class="bi bi-mortarboard-fill text-warning me-1"></i>
                    MVC Architecture &bull; BCrypt Hashing &bull; Connection Pooling &bull; Async Concurrency
                </div>
            </div>
        </div>

        <hr class="my-4 border-secondary border-opacity-25">

        <div class="d-flex flex-wrap justify-content-between align-items-center text-white-50 small">
            <div>
                &copy; 2026 GardenConnect. Open source under the MIT License.
            </div>
            <div>
                Powered by Jakarta Servlet Architecture & Apache Tomcat 10.1
            </div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5.3 JS Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

<!-- Chart.js 4.4 CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.2/dist/chart.umd.min.js"></script>

<!-- Custom Application Scripts -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/charts.js"></script>
</body>
</html>
