package filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtro de autenticación para proteger páginas privadas. Verifica si el
 * usuario está logueado antes de permitir acceso.
 */
@WebFilter(filterName = "FiltroAutenticacion", urlPatterns = {"/*"})
public class FiltroAutenticacion implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // Permitir acceso a login.html y recursos públicos sin filtro
        if (path.endsWith("login.html") || path.endsWith("LoginServlet") || path.contains("/css/") || path.contains("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        String usuario = (session != null) ? (String) session.getAttribute("usuario") : null;

        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + "/login.html");
        } else {
            chain.doFilter(request, response);
        }
    }
}
