<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Standard MVC entry point: forward request to the HomeController
    request.getRequestDispatcher("/home").forward(request, response);
%>
