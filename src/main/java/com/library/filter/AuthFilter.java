package com.library.filter;

import com.library.model.User;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/** Session login check + role-based access + CSRF token validation + basic security headers. */
@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse res = (HttpServletResponse) rs;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("Cache-Control", "no-store");

        HttpSession session = req.getSession(true);
        if (session.getAttribute("csrf") == null) session.setAttribute("csrf", UUID.randomUUID().toString());

        if ("POST".equals(req.getMethod())) {
            String token = req.getParameter("csrf");
            if (token == null || !token.equals(session.getAttribute("csrf"))) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                return;
            }
        }

        boolean isPublic = path.equals("/login") || path.equals("/register");
        User user = (User) session.getAttribute("user");

        if (!isPublic && user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (path.startsWith("/admin") && (user == null || !user.isAdmin())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }
        chain.doFilter(rq, rs);
    }
}
