package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import service.UserService;
import service.UserServiceImpl;

import java.io.IOException;

@WebServlet(value = "/registration")
public class RegistrationServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("isError", null);
        getServletContext().getRequestDispatcher("/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isDone = registration(req, resp);
        HttpSession session = req.getSession();
        if (isDone) {
            session.setAttribute("isError", null);
            resp.sendRedirect(req.getContextPath() + "/login");
//            getServletContext().getRequestDispatcher("/login-test.jsp").forward(req, resp);
        }
        else {
            getServletContext().getRequestDispatcher("/registration.jsp").forward(req, resp);
        }
    }

    private boolean registration(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirm_password");
        User user = userService.getUser(username);
        System.out.println(user);
        if (user != null) {
            session.setAttribute("ErrorMessage", "User " + username + " already exist!");
            session.setAttribute("isError", true);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            session.setAttribute("ErrorMessage", "Password confirmation failed");
            session.setAttribute("isError", true);
            return false;
        }
        userService.setUser(username,password);
        session.setAttribute("SuccessMessage", "User " + username + " has been successfully registered, please log in");
        session.setAttribute("isSuccess", true);
        return true;
    }
}
