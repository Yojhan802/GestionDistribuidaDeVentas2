/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import dto.Cliente;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "ClienteServlet", urlPatterns = {"/cliente"})
public class ClienteServlet extends HttpServlet {

    @PersistenceUnit(unitName = "com.mycompany_TPD06_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            List<Cliente> clientes = em.createNamedQuery("Cliente.findAll", Cliente.class).getResultList();
            JSONArray jsonArray = new JSONArray();
            for (Cliente c : clientes) {
                JSONObject obj = new JSONObject();
                obj.put("codiClie", c.getCodiClie());
                obj.put("nombClie", c.getNombClie());
                jsonArray.put(obj);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonArray.toString());
            out.flush();
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            int codiClie = jsonObject.getInt("codiClie");
            String nombClie = jsonObject.getString("nombClie");

            // Verificar si ya existe un cliente con el mismo código
            Cliente clienteExistente = em.find(Cliente.class, codiClie);

            if (clienteExistente != null) {
                // Si el cliente ya existe, enviar un código de estado 409 (Conflict)
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                JSONObject errorJson = new JSONObject();
                errorJson.put("mensaje", "El código de cliente ya existe en la base de datos.");
                out.print(errorJson.toString());
                out.flush();
            } else {
                // Si el cliente no existe, crear y persistir el nuevo cliente
                Cliente cliente = new Cliente();
                cliente.setCodiClie(codiClie);
                cliente.setNombClie(nombClie);

                em.getTransaction().begin();
                em.persist(cliente);
                em.getTransaction().commit();

                // Enviar un código de estado 201 (Created) para indicar éxito
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                JSONObject successJson = new JSONObject();
                successJson.put("mensaje", "Cliente agregado con éxito.");
                out.print(successJson.toString());
                out.flush();
            }

        } catch (Exception e) {
            em.getTransaction().rollback();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // Opcionalmente, podrías enviar un mensaje de error más detallado en la respuesta
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            int codiClie = jsonObject.getInt("codiClie");
            Cliente cliente = em.find(Cliente.class, codiClie);

            if (cliente != null) {
                cliente.setNombClie(jsonObject.getString("nombClie"));
                em.getTransaction().begin();
                em.merge(cliente);
                em.getTransaction().commit();
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = getEntityManager();
        try {
            int codiClie = Integer.parseInt(request.getParameter("codiClie"));
            Cliente cliente = em.find(Cliente.class, codiClie);

            if (cliente != null) {
                em.getTransaction().begin();
                em.remove(cliente);
                em.getTransaction().commit();
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            em.close();
        }
    }
}
