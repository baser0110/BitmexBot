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

@WebServlet (value = "/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/login-test.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isDone = authenticate(req, resp);
        HttpSession session = req.getSession();
        if (isDone) {
            session.setAttribute("isError", null);
            session.setAttribute("isSuccess", null);
            resp.sendRedirect(req.getContextPath() + "/app");
//            getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
        }
        else
            getServletContext().getRequestDispatcher("/login-test.jsp").forward(req,resp);
    }

    private boolean authenticate(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        User user = userService.getUser(username);
        if (user == null) {
            session.setAttribute("ErrorMessage", "User " + username + " not found!");
            session.setAttribute("isError", true);
            return false;
        }
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            session.setAttribute("User", user);
            session.setAttribute("isError", false);
        }
        else {
            session.setAttribute("ErrorMessage", "Wrong password!");
            session.setAttribute("isError", true);
            return false;
            }
        return true;
        }
}
