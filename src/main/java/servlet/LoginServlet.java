package servlet;

import dao.UsuarioJpaController;
import dto.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
    String password = request.getParameter("password");

    response.setContentType("text/plain;charset=UTF-8");

    if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
        response.getWriter().write("Por favor complete todos los campos.");
        return;
    }

    UsuarioJpaController usuarioController = new UsuarioJpaController();
    Usuario user = usuarioController.findUsuarioByLoginAndPassword(username, password);

    if (user != null) {
        HttpSession session = request.getSession();
        session.setAttribute("usuario", user);
        response.getWriter().write("OK");
    } else {
        response.getWriter().write("Usuario o contraseña incorrectos.");
    }
    }

    @Override
    public String getServletInfo() {
        return "Servlet de inicio de sesión con validación";
    }
}
