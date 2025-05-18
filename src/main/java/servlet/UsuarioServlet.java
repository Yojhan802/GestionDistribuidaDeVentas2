package servlet;

import com.google.gson.Gson;
import dto.Usuario;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    private EntityManager em;
    private Gson gson = new Gson();

    @Override
    public void init() {
        em = emf.createEntityManager();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Usuario> usuarios = em.createNamedQuery("Usuario.findAll", Usuario.class).getResultList();
        String json = gson.toJson(usuarios);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        Usuario nuevo = gson.fromJson(reader, Usuario.class);

        // Autoincrementar manualmente si es necesario
        Integer maxId = (Integer) em.createQuery("SELECT COALESCE(MAX(u.codiUsua), 0) FROM Usuario u").getSingleResult();
        nuevo.setCodiUsua(maxId + 1);

        em.getTransaction().begin();
        em.persist(nuevo);
        em.getTransaction().commit();

        resp.setContentType("application/json");
        resp.getWriter().write("{\"mensaje\": \"Usuario creado correctamente\"}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        Usuario actualizado = gson.fromJson(reader, Usuario.class);

        em.getTransaction().begin();
        Usuario existente = em.find(Usuario.class, actualizado.getCodiUsua());
        if (existente != null) {
            existente.setLogiUsua(actualizado.getLogiUsua());
            existente.setPassUsua(actualizado.getPassUsua());
            em.merge(existente);
            em.getTransaction().commit();
            resp.getWriter().write("{\"mensaje\": \"Usuario actualizado\"}");
        } else {
            em.getTransaction().rollback();
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        em.getTransaction().begin();
        Usuario u = em.find(Usuario.class, id);
        if (u != null) {
            em.remove(u);
            em.getTransaction().commit();
            resp.getWriter().write("{\"mensaje\": \"Usuario eliminado\"}");
        } else {
            em.getTransaction().rollback();
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
        }
    }

    @Override
    public void destroy() {
        em.close();
        emf.close();
    }
}
