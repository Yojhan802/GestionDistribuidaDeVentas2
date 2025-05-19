package servlet;

import dto.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import javax.persistence.NoResultException;

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

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.sendRedirect("login.html?error=Por%20favor%20complete%20todos%20los%20campos");
            return;
        }

        EntityManager em = emf.createEntityManager();
        Usuario user = null;

        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.logiUsua = :username AND u.passUsua = :password", Usuario.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            user = query.getSingleResult();

        } catch (NoResultException e) {
            // No hace falta hacer nada, el usuario quedará como null
        } catch (Exception e) {
            response.sendRedirect("login.html?error=Error%20en%20la%20consulta");
            return;
        } finally {
            em.close();
        }

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario", user);
            response.sendRedirect(request.getContextPath() + "/index.html");
        } else {
            response.sendRedirect("login.html?error=Usuario%20o%20contraseña%20incorrectos");
        }

    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
    }
}
