package servlet;

import exception.WrongKeyException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FibonacciOrderSet;
import entity.User;
import model.OrderGetTest;
import service.*;

import java.io.IOException;

@WebServlet(value = {"/app","/app-start","/app-cancel","/app-logout","/app-setKey"})
public class AppServlet extends HttpServlet {
    private UserService userService;
    private BotService app;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAppLogic(req, resp);
    }

    private void doAppLogic(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getContextPath();
        String reqURI = req.getRequestURI();
        if (reqURI.equals(path + "/app-setKey")) { keyUpdate(req,resp); }
        if (reqURI.equals(path + "/app-logout")) { logout(req,resp); }
        if (reqURI.equals(path + "/app-cancel")) { cancel(req,resp); }
        if (reqURI.equals(path + "/app-start")) { start(req,resp); }

    }

    private void keyUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("isError", null);
        session.setAttribute("isSuccess", null);
        String apiKey = req.getParameter("api-key");
        String secretKey = req.getParameter("secret-key");
        User user = (User) session.getAttribute("User");
        try {
            OrderGetTest get = new OrderGetTest(1.);
            OrderHttpService.send(get, secretKey, apiKey);
        } catch (WrongKeyException e) {
            session.setAttribute("ErrorMessage", e.getMessage());
            session.setAttribute("isError", true);
            getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
            return;
        }
        if (user == null) {
            session.setAttribute("ErrorMessage", "Some shit, we don't know");
            session.setAttribute("isError", true);
            getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
            return;
        }
        userService.setKeysForUser(user.getUsername(), secretKey, apiKey);
        session.setAttribute("SuccessMessage", "Keys have been successfully updated");
        session.setAttribute("isSuccess", true);
        user = userService.getUser(user.getUsername());
        session.setAttribute("User", user);
        getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
    }

    private boolean logout(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        try {
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean cancel(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("isError", null);
        session.setAttribute("isSuccess", null);
        if (app != null) {
            app.stop();
            session.setAttribute("SuccessMessage", "Bot has been successfully stopped");
            session.setAttribute("isSuccess", true);
            getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
        } else {
            session.setAttribute("ErrorMessage", "Bot is not started");
            session.setAttribute("isError", true);
            getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
        }
        return false;
    }

    private boolean start(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("isError", null);
        session.setAttribute("isSuccess", null);
        User user = (User) session.getAttribute("User");
        String apiKey = user.getApiKey();
        String secretKey = user.getSecretKey();
        int level = Integer.parseInt(req.getParameter("level"));
        System.out.println(level);
        double step = Double.parseDouble(req.getParameter("step"));
        System.out.println(step);
        double size = Double.parseDouble(req.getParameter("size"));
        System.out.println(size);
        BotLogic logic = new FibonacciOrderService(new FibonacciOrderSet(level,step,size),secretKey,apiKey);
        app = new BotService(logic,secretKey,apiKey);
        try {
            app.run();
            session.setAttribute("SuccessMessage", "Bot has been successfully started");
            session.setAttribute("isSuccess", true);
            getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
        } catch (WrongKeyException e) {
            session.setAttribute("ErrorMessage", e.getMessage());
            session.setAttribute("isError", true);
            getServletContext().getRequestDispatcher("/app.jsp").forward(req,resp);
        }
        return true;
    }

}
