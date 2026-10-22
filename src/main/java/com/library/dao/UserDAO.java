package com.library.dao;

import com.library.model.User;
import com.library.util.DBUtil;
import com.library.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("role"));
    }

    /** Returns the user if email+password match, otherwise null. */
    public User authenticate(String email, String password) throws SQLException {
        try (Connection c = DBUtil.get();
             PreparedStatement ps = c.prepareStatement("SELECT id,name,email,role,password_hash FROM users WHERE email=?")) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(password, rs.getString("password_hash"))) return map(rs);
            }
        }
        return null;
    }

    /** @return false if the email already exists */
    public boolean create(String name, String email, String password, String role) throws SQLException {
        try (Connection c = DBUtil.get();
             PreparedStatement ps = c.prepareStatement("INSERT INTO users(name,email,password_hash,role) VALUES(?,?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, email.toLowerCase());
            ps.setString(3, PasswordUtil.hash(password));
            ps.setString(4, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) return false;
            throw e;
        }
    }

    public boolean adminExists() throws SQLException {
        try (Connection c = DBUtil.get(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1 FROM users WHERE role='ADMIN' LIMIT 1")) {
            return rs.next();
        }
    }

    public List<User> members() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection c = DBUtil.get(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name,email,role FROM users WHERE role='MEMBER' ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
}
