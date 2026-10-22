package com.library.servlet;

import com.library.dao.BookDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Book catalogue + search by title / author / category (all logged-in users). */
@WebServlet("/books")
public class BookServlet extends HttpServlet {
    private final BookDAO books = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String f = req.getParameter("f"), q = req.getParameter("q");
        try {
            req.setAttribute("books", books.search(f, q));
        } catch (SQLException e) { throw new ServletException(e); }
        req.setAttribute("f", f == null ? "title" : f);
        req.setAttribute("q", q == null ? "" : q);
        req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);
    }
}
