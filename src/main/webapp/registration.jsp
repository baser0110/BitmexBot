<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Registration Page</title>

    <link rel="stylesheet"
          href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
          crossorigin="anonymous">
</head>

</head>
<body>

<div class="container col-md-8 col-md-offset-3" style="overflow: auto">
        <hr>
            <h1>User Registration Form</h1>
                <form action="<%=request.getContextPath()%>/registration" method="post">

                    <div class="form-group">
                        <label for="username">Username:</label> <input type="text"
                                                                      class="form-control" id="username" placeholder="username"
                                                                      name="username" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Password:</label> <input type="password"
                                                                     class="form-control" id="password" placeholder="password"
                                                                     name="password" required>
                    </div>
                    <div class="form-group">
                        <label for="confirm_password">Confirm password:</label> <input type="password"
                                                                    class="form-control" id="confirm_password" placeholder="confirm password"
                                                                    name="confirm_password" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Submit</button>

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
                </form>
    <hr>
</div>
</body>
</html>