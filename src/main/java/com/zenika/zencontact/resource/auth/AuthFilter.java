package com.zenika.zencontact.resource.auth;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author Clément Garbay
 */
@WebFilter(urlPatterns = {"api/v0/users/*"})
public class AuthFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            // pathParts[1] = {id}
            String[] pathParts = pathInfo.split("/");

            final AuthenticationService authenticationService = AuthenticationService.getInstance();

            // only admin can delete
            if ("DELETE".equals(request.getMethod()) && !authenticationService.isAdmin()) {
                response.setStatus(SC_FORBIDDEN);
                return;
            }

            if (authenticationService.isAuthenticated()
                && authenticationService.getUsername() != null) {
                // user is already connected
                response.setHeader("Username", authenticationService.getUsername());
                response.setHeader("Logout", authenticationService.getLogoutURL("/#/clear"));
            } else {
                // only authenticate users can edit
                response.setHeader("Location", authenticationService.getLoginURL("/#/edit/" + pathParts[1]));
                response.setHeader("Logout", authenticationService.getLogoutURL("/#/clear"));
                response.setStatus(SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
