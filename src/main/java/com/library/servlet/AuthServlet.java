package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final UserDAO users = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getServletPath();
        if (p.equals("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User u = (User) req.getSession().getAttribute("user");
        if (u != null && p.equals("/login")) { redirectHome(req, resp, u); return; }
        req.getRequestDispatcher("/WEB-INF/views/" + (p.equals("/register") ? "register" : "login") + ".jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = trim(req.getParameter("email")), pass = req.getParameter("password");
        try {
            if (req.getServletPath().equals("/register")) {
                String name = trim(req.getParameter("name"));
                String error = null;
                if (name.isEmpty() || name.length() > 100) error = "Enter a valid name.";
                else if (!EMAIL.matcher(email).matches()) error = "Enter a valid email.";
                else if (pass == null || pass.length() < 8) error = "Password must be at least 8 characters.";
                else if (!users.create(name, email, pass, "MEMBER")) error = "Email already registered.";
                if (error != null) {
                    req.setAttribute("error", error);
                    req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/login?registered=1");
                }
                return;
            }
            User u = (pass == null) ? null : users.authenticate(email, pass);
            if (u == null) {
                req.setAttribute("error", "Invalid email or password.");
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }
            req.getSession().invalidate();              // prevent session fixation
            req.getSession(true).setAttribute("user", u);
            redirectHome(req, resp, u);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void redirectHome(HttpServletRequest req, HttpServletResponse resp, User u) throws IOException {
        resp.sendRedirect(req.getContextPath() + (u.isAdmin() ? "/admin/dashboard" : "/books"));
    }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
