package com.facultyleave.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class HODAuthFilter extends HttpFilter implements Filter {

    @Override
    protected void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        String contextPath = request.getContextPath();

        String requestPath =
                request.getRequestURI().substring(contextPath.length());

        // Protect every HOD page, JSP, and future HOD endpoint
        if (requestPath.startsWith("/hod")) {

            HttpSession session = request.getSession(false);

            // No logged-in user
            if (session == null) {
                response.sendRedirect("login.html?error=unauthorized");
                return;
            }

            Object roleObject = session.getAttribute("userRole");

            // Logged-in user is not HOD
            if (roleObject == null
                    || !"HOD".equals(roleObject.toString())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Access denied. HOD access only."
                );

                return;
            }
        }

        // Continue normally
        chain.doFilter(request, response);
    }
}