import dto.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.io.IOException;


@WebServlet(name = "LoginServlet", urlPatterns = {"/Servlet"})
public class LoginServlet extends HttpServlet {

    private EntityManagerFactory emf;
    
    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if(username == null || password == null || username.isEmpty() || password.isEmpty()){
            request.setAttribute("error", "Por favor complete todos los campos.");
            request.getRequestDispatcher("login.html").forward(request, response);
            return;
        }
        
        EntityManager em = emf.createEntityManager();
        
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.logiUsua = :username AND u.passUsua = :password", Usuario.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            
            Usuario user = null;
            try {
                user = query.getSingleResult();
            } catch (Exception e) {
                // No se encontró usuario, user queda null
            }
            
            if(user != null){
                HttpSession session = request.getSession();
                session.setAttribute("usuario", user);
                response.sendRedirect("index.html"); 
            } else {
                request.setAttribute("error", "Usuario o contraseña incorrectos.");
                request.getRequestDispatcher("login.html").forward(request, response);
            }
        } finally {
            em.close();
        }
    }
    
    @Override
    public void destroy() {
        if(emf != null){
            emf.close();
        }
    }
}