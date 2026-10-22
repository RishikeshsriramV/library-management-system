package com.library.servlet;

import com.library.dao.*;
import com.library.model.Book;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Admin only (enforced by AuthFilter): dashboard, book CRUD, issue and return. */
@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private final BookDAO books = new BookDAO();
    private final IssueDAO issues = new IssueDAO();
    private final UserDAO users = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getPathInfo() == null ? "/dashboard" : req.getPathInfo();
        try {
            switch (p) {
                case "/dashboard":
                    req.setAttribute("stats", issues.stats());
                    req.setAttribute("active", issues.active());
                    req.setAttribute("members", users.members());
                    req.setAttribute("books", books.search("title", ""));
                    forward(req, resp, "dashboard");
                    break;
                case "/book/new":
                    forward(req, resp, "bookForm");
                    break;
                case "/book/edit":
                    Book b = books.find(intParam(req, "id"));
                    if (b == null) { resp.sendError(404); return; }
                    req.setAttribute("book", b);
                    forward(req, resp, "bookForm");
                    break;
                default:
                    resp.sendError(404);
            }
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getPathInfo() == null ? "" : req.getPathInfo();
        String dest = "/admin/dashboard";
        try {
            switch (p) {
                case "/book/save": {
                    int id = intParam(req, "id"), total = intParam(req, "total");
                    String title = s(req, "title"), author = s(req, "author"), cat = s(req, "category"), isbn = s(req, "isbn");
                    dest = "/books";
                    if (title.isEmpty() || author.isEmpty() || cat.isEmpty() || total < 0) {
                        flash(req, "danger", "Title, author, category and a valid copy count are required.");
                    } else if (id > 0) {
                        if (books.update(id, title, author, cat, isbn, total)) flash(req, "success", "Book updated.");
                        else flash(req, "danger", "Total copies cannot be lower than copies currently issued.");
                    } else {
                        books.add(title, author, cat, isbn, total);
                        flash(req, "success", "Book added.");
                    }
                    break;
                }
                case "/book/delete":
                    dest = "/books";
                    if (books.delete(intParam(req, "id"))) flash(req, "success", "Book deleted.");
                    else flash(req, "danger", "Cannot delete: this book has issue records.");
                    break;
                case "/issue": {
                    String err = issues.issue(intParam(req, "bookId"), intParam(req, "userId"));
                    if (err == null) flash(req, "success", "Book issued. Due in 14 days.");
                    else flash(req, "danger", err);
                    break;
                }
                case "/return": {
                    int fine = issues.returnBook(intParam(req, "issueId"));
                    if (fine < 0) flash(req, "danger", "Invalid or already returned.");
                    else flash(req, fine > 0 ? "warning" : "success", fine > 0 ? "Returned late. Fine: Rs. " + fine : "Returned on time. No fine.");
                    break;
                }
                default:
                    resp.sendError(404); return;
            }
        } catch (SQLException e) { throw new ServletException(e); }
        resp.sendRedirect(req.getContextPath() + dest);
    }

    private void forward(HttpServletRequest rq, HttpServletResponse rs, String view) throws ServletException, IOException {
        rq.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(rq, rs);
    }
    private static void flash(HttpServletRequest req, String type, String msg) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flash", msg);
    }
    private static String s(HttpServletRequest r, String n) { String v = r.getParameter(n); return v == null ? "" : v.trim(); }
    private static int intParam(HttpServletRequest r, String n) {
        try { return Integer.parseInt(r.getParameter(n)); } catch (Exception e) { return -1; }
    }
}
