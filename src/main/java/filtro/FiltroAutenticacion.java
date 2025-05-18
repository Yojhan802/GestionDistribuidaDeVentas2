package filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtro de autenticación para proteger páginas privadas.
 * Verifica si el usuario está logueado antes de permitir acceso.
 */
@WebFilter(filterName = "FiltroAutenticacion", urlPatterns = {"/index.html","/cliente.html","/detalle.html","/kardex.html","/producto.html","/usuario.html","/venta.html"})
public class FiltroAutenticacion implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        
        // Convertir a objetos HTTP para trabajar con sesión y redirección
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Obtener sesión actual, sin crear una nueva si no existe
        HttpSession session = req.getSession(false);

        // Comprobar si hay un usuario logueado en sesión
        boolean usuarioLogueado = (session != null && session.getAttribute("usuario") != null);

        if (usuarioLogueado) {
            // Usuario autenticado: dejar continuar la petición
            chain.doFilter(request, response);
        } else {
            // No está autenticado: redirigir a la página de login
            res.sendRedirect("login.html");
        }
    }
}
