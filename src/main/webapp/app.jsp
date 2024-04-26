<%@ page import="entity.User" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%
    User user = (User) session.getAttribute("User");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>BitmexBotApp</title>
    <link rel="stylesheet"
          href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
          crossorigin="anonymous">
</head>
<body>

<div class="container col-md-8 col-md-offset-3" style="overflow: auto">
    <hr>
    <h1>BitmexBot (XBTUSD)</h1>
    <h2><button form="logout" type="submit" class="btn btn-secondary">Logout</button>
        User: <%= user.getUsername() %></h2>
    <h4></h4>
    <form action="${pageContext.request.contextPath}/app-setKey" method="post" id="setKey">
        <%
            String apiKeyInfo = user.getApiKey() == null ? "Not_defined" : user.getApiKey().toString();
        %>
        <div class="form-group">
            <label for="api-key">API key:</label> <input type="text"
                                                           class="form-control" id="api-key" placeholder=<%= apiKeyInfo %>
                                                           name="api-key" required>
        </div>
        <%
            String secretKeyInfo = (user.getSecretKey() == null ? "Not_defined" : user.getSecretKey().toString());
        %>
        <div class="form-group">
            <label for="secret-key">Secret key:</label> <input type="text"
                                                          class="form-control" id="secret-key" placeholder=<%= secretKeyInfo %>
                                                          name="secret-key" required>
        </div>
        <button type="submit" class="btn btn-primary">Set keys</button>
    </form>
    <h4></h4>
    <h2> Fibonacci logic starting parameters: </h2>
    <form action="${pageContext.request.contextPath}/app-start" method="post" id="start">
        <div class="form-group">
            <label for="level">Level:</label> <input type="text"
                                                         class="form-control" id="level" placeholder="1,2,3,4 ..."
                                                                 name="level" required>
        </div>
        <div class="form-group">
            <label for="step">Step:</label> <input type="text"
                                                               class="form-control" id="step" placeholder="The step must be a multiple of 100"
                                                                       name="step" required>
        </div>
        <div class="form-group">
            <label for="size">Size:</label> <input type="text"
                                                               class="form-control" id="size" placeholder="The size must be a multiple of 100"
                                                                       name="size" required>
        </div>
        <button type="submit" class="btn btn-primary">Start bot</button>
        <button form="cancel" type="submit" class="btn btn-danger">Cancel bot</button>
    </form>
    <form action="${pageContext.request.contextPath}/app-cancel" method="post" id="cancel" name="pageName" values="login">
    </form>
    <form action="${pageContext.request.contextPath}/app-logout" method="post" id="logout">
        <%
            String alert = null;
            Boolean isError = (Boolean) session.getAttribute("isError");
            if (isError == null) isError = false;
            if (isError) alert = session.getAttribute("ErrorMessage").toString();
        %>

        <h2></h2>
        <div <%=isError ? "" : "hidden"%> class="alert alert-danger" role="alert" id="alert-danger">
            <b> <%= alert %> </b>
        </div>

        <%
            String success = null;
            Boolean isSuccess = (Boolean) session.getAttribute("isSuccess");
            if (isSuccess == null) isSuccess = false;
            if (isSuccess) success = session.getAttribute("SuccessMessage").toString();
        %>

        <h2></h2>
        <div <%=isSuccess ? "" : "hidden"%> class="alert alert-success" role="alert" id="alert-success">
            <b> <%= success %> </b>
        </div>
    </form>
    <hr>
</div>
</body>
</html>