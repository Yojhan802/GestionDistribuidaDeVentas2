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
        UsuarioJpaController control = new UsuarioJpaController();
        String usuario = request.getParameter("usuario");
        String contrasenia = request.getParameter("contrasenia");
        
        boolean validacion = false;
        
        validacion = control.comprobaringreso(usuario,contrasenia);
        
         if (validacion == true) {
             HttpSession misession = request.getSession(true);
             misession.setAttribute("usuario", usuario);
             response.sendRedirect("index.html");
         }else{
             response.sendRedirect("login.html");
         }
         

        
    }

    @Override
    public String getServletInfo() {
        return "Servlet de inicio de sesión con validación";
    }
}