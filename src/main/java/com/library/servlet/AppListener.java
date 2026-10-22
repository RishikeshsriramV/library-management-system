package com.library.servlet;

import com.library.dao.UserDAO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Seeds the first admin account on startup (credentials from env vars in production). */
@WebListener
public class AppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent e) {
        try {
            UserDAO dao = new UserDAO();
            if (!dao.adminExists()) {
                String email = System.getenv().getOrDefault("ADMIN_EMAIL", "admin@library.com");
                String pass = System.getenv().getOrDefault("ADMIN_PASSWORD", "Admin@123");
                dao.create("Administrator", email, pass, "ADMIN");
                e.getServletContext().log("Default admin created: " + email + " (change the password!)");
            }
        } catch (Exception ex) {
            e.getServletContext().log("Admin seeding failed", ex);
        }
    }
}
