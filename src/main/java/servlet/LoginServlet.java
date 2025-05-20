
package servlet;

import conexion.Conexion;
import dao.UsuarioJpaController;

import java.io.IOException;
import java.sql.Connection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
Connection con = Conexion.getConnection();
  UsuarioJpaController control = new UsuarioJpaController();
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
        return "Short description";
    }// </editor-fold>

}
