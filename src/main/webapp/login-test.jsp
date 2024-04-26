<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Login Page</title>
    <link rel="stylesheet"
          href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
          crossorigin="anonymous">
</head>
<body>

<div class="container col-md-8 col-md-offset-3" style="overflow: auto">
    <hr>
    <h1>Login to BitmexBot</h1>
    <form id="loginForm" action="<%=request.getContextPath()%>/login" method="post">
        <div class="form-group">
            <label for="username">Username:</label> <input type="text"
                                                         class="form-control" id="username" placeholder="Username"
                                                         name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Password:</label> <input type="password"
                                                        class="form-control" id="password" placeholder="Password"
                                                        name="password" required>
        </div>
        <button type="submit" class="btn btn-primary">Login</button>
        <button form="toRegistrationForm" type="submit" class="btn btn-success">Registration</button>
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
    <form id="toRegistrationForm" action="<%=request.getContextPath()%>/registration" method="get">

    </form>
    <hr>

</div>
</body>
</html>