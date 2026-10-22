package com.library.dao;

import com.library.model.Book;
import com.library.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Book map(ResultSet rs) throws SQLException {
        return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("category"),
                rs.getString("isbn"), rs.getInt("total_copies"), rs.getInt("available_copies"));
    }

    /** field must be title | author | category (whitelisted, never concatenated from user input). */
    public List<Book> search(String field, String q) throws SQLException {
        String col = "author".equals(field) ? "author" : "category".equals(field) ? "category" : "title";
        List<Book> list = new ArrayList<>();
        try (Connection c = DBUtil.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE " + col + " LIKE ? ORDER BY title")) {
            ps.setString(1, "%" + (q == null ? "" : q.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public Book find(int id) throws SQLException {
        try (Connection c = DBUtil.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public void add(String title, String author, String category, String isbn, int total) throws SQLException {
        try (Connection c = DBUtil.get();
             PreparedStatement ps = c.prepareStatement("INSERT INTO books(title,author,category,isbn,total_copies,available_copies) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, title); ps.setString(2, author); ps.setString(3, category); ps.setString(4, isbn);
            ps.setInt(5, total); ps.setInt(6, total);
            ps.executeUpdate();
        }
    }

    /** Adjusts available copies by the change in total; refuses if that would drop below copies already issued. */
    public boolean update(int id, String title, String author, String category, String isbn, int total) throws SQLException {
        String sql = "UPDATE books SET title=?,author=?,category=?,isbn=?," +
                "available_copies=available_copies+(?-total_copies), total_copies=? " +
                "WHERE id=? AND available_copies+(?-total_copies)>=0";
        try (Connection c = DBUtil.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title); ps.setString(2, author); ps.setString(3, category); ps.setString(4, isbn);
            ps.setInt(5, total); ps.setInt(6, total); ps.setInt(7, id); ps.setInt(8, total);
            return ps.executeUpdate() == 1;
        }
    }

    /** @return false if the book has issue history (FK restriction) */
    public boolean delete(int id) throws SQLException {
        try (Connection c = DBUtil.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }
    }
}
