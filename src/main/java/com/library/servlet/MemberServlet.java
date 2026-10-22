package com.library.servlet;

import com.library.dao.IssueDAO;
import com.library.model.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Member-wise borrowing history with due dates and fines. */
@WebServlet("/member/history")
public class MemberServlet extends HttpServlet {
    private final IssueDAO issues = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = (User) req.getSession().getAttribute("user");
        try {
            req.setAttribute("history", issues.history(u.getId()));
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(req, resp);
    }
}
