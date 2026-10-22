package com.library.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public final class DBUtil {
    private static final Properties P = new Properties();
    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            P.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
    private DBUtil() {}
    public static Connection get() throws SQLException {
        String url = cfg("DB_URL", "db.url");
        String user = cfg("DB_USER", "db.user");
        String pass = cfg("DB_PASSWORD", "db.password");
        System.out.println("DB DEBUG url=" + url + " user=[" + user + "] passLen=" + pass.length()
                + " envPassSet=" + (System.getenv("DB_PASSWORD") != null));
        return DriverManager.getConnection(url, user, pass);
    }

    private static String cfg(String env, String key) {
        String v = P.getProperty(key);
        if (v == null || v.trim().isEmpty()) v = System.getenv(env);
        return v == null ? null : v.trim();
    }
}