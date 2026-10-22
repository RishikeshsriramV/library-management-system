package com.library.dao;

import com.library.model.Issue;
import com.library.util.DBUtil;
import java.sql.*;
import java.util.*;

public class IssueDAO {
    private static final String SELECT =
            "SELECT i.id,i.book_id,b.title,u.name,i.issue_date,i.due_date,i.return_date,i.fine " +
            "FROM issues i JOIN books b ON b.id=i.book_id JOIN users u ON u.id=i.user_id ";

    private List<Issue> query(String where, Integer param) throws SQLException {
        List<Issue> list = new ArrayList<>();
        try (Connection c = DBUtil.get(); PreparedStatement ps = c.prepareStatement(SELECT + where)) {
            if (param != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Issue(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getDate(5), rs.getDate(6), rs.getDate(7), rs.getInt(8)));
            }
        }
        return list;
    }
    public List<Issue> active() throws SQLException { return query("WHERE i.return_date IS NULL ORDER BY i.due_date", null); }
    public List<Issue> history(int userId) throws SQLException { return query("WHERE i.user_id=? ORDER BY i.issue_date DESC, i.id DESC", userId); }

    /** Issues a book inside one transaction. @return null on success, otherwise an error message. */
    public String issue(int bookId, int userId) throws SQLException {
        try (Connection c = DBUtil.get()) {
            c.setAutoCommit(false);
            try {
                int available;
                try (PreparedStatement ps = c.prepareStatement("SELECT available_copies FROM books WHERE id=? FOR UPDATE")) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { c.rollback(); return "Book not found."; }
                        available = rs.getInt(1);
                    }
                }
                if (available <= 0) { c.rollback(); return "No copies available."; }
                try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM issues WHERE book_id=? AND user_id=? AND return_date IS NULL")) {
                    ps.setInt(1, bookId); ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) { c.rollback(); return "Member already has this book."; }
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE id=?")) {
                    ps.setInt(1, bookId); ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO issues(book_id,user_id,issue_date,due_date) VALUES(?,?,CURDATE(),DATE_ADD(CURDATE(),INTERVAL ? DAY))")) {
                    ps.setInt(1, bookId); ps.setInt(2, userId); ps.setInt(3, Issue.LOAN_DAYS);
                    ps.executeUpdate();
                }
                c.commit();
                return null;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    /** Returns a book, calculates the fine, restores stock - all in one transaction. @return fine, or -1 if invalid. */
    public int returnBook(int issueId) throws SQLException {
        try (Connection c = DBUtil.get()) {
            c.setAutoCommit(false);
            try {
                int bookId, fine;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT book_id, GREATEST(DATEDIFF(CURDATE(),due_date),0) FROM issues WHERE id=? AND return_date IS NULL FOR UPDATE")) {
                    ps.setInt(1, issueId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { c.rollback(); return -1; }
                        bookId = rs.getInt(1);
                        fine = rs.getInt(2) * Issue.FINE_PER_DAY;
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE issues SET return_date=CURDATE(), fine=? WHERE id=?")) {
                    ps.setInt(1, fine); ps.setInt(2, issueId); ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE books SET available_copies=available_copies+1 WHERE id=?")) {
                    ps.setInt(1, bookId); ps.executeUpdate();
                }
                c.commit();
                return fine;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    public Map<String, Object> stats() throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        try (Connection c = DBUtil.get()) {
            m.put("books", scalar(c, "SELECT COUNT(*) FROM books"));
            m.put("members", scalar(c, "SELECT COUNT(*) FROM users WHERE role='MEMBER'"));
            m.put("active", scalar(c, "SELECT COUNT(*) FROM issues WHERE return_date IS NULL"));
            m.put("overdue", scalar(c, "SELECT COUNT(*) FROM issues WHERE return_date IS NULL AND due_date < CURDATE()"));
            m.put("fines", scalar(c, "SELECT COALESCE(SUM(fine),0) FROM issues"));
        }
        return m;
    }
    private Object scalar(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) { rs.next(); return rs.getObject(1); }
    }
}
