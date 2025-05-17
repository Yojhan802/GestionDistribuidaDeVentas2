package servlet;

import dto.Producto;
import java.io.BufferedReader;
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

/**
 *
 * @author ANDREA
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/productoservlet"})
public class ProductoServlet extends HttpServlet {
    
    //Colocar la persistencia del proyecto
    @PersistenceUnit(unitName = "com.mycompany_TPD06_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //Optener datos - Listar
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        //Obtenemos persistencia
        EntityManager em = getEntityManager();
        try {
            //Lista productos
            List<Producto> productos = em.createNamedQuery("Producto.findAll", Producto.class).getResultList();
            //Crea objeto json
            JSONArray jsonArray = new JSONArray();
            //Conversion en formato json
            for (Producto p : productos) {
                JSONObject obj = new JSONObject();
                obj.put("codiProd", p.getCodiProd());
                obj.put("nombProd", p.getNombProd());
                obj.put("precProd", p.getPrecProd());
                obj.put("stocProd", p.getStocProd());
                jsonArray.put(obj);
            }
            //Envia respuesta
            response.setContentType("application/json");
            //Valida en español
            response.setCharacterEncoding("UTF-8");
            //Imprime
            PrintWriter out = response.getWriter();
            out.print(jsonArray.toString());
            //Envia datos
            out.flush();
        } finally {
            em.close();
        }
    }

    //Agregar
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        EntityManager em = getEntityManager();
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        //Recibe datos en json
        JSONObject json = new JSONObject(jsonBuilder.toString());
        Producto producto = new Producto();
        int nuevoCodigo = em.createQuery("SELECT COALESCE(MAX(p.codiProd), 0) FROM Producto p", Integer.class).getSingleResult() + 1;
        producto.setCodiProd(nuevoCodigo);
        producto.setNombProd(json.getString("nombProd"));
        producto.setPrecProd(json.getDouble("precProd"));
        producto.setStocProd(json.getDouble("stocProd"));

        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            em.getTransaction().rollback();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            em.close();
        }
    }

    //Modificar
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        EntityManager em = getEntityManager();
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        JSONObject json = new JSONObject(jsonBuilder.toString());
        int codiProd = json.getInt("codiProd");

        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, codiProd);
            if (producto != null) {
                producto.setNombProd(json.getString("nombProd"));
                producto.setPrecProd(json.getDouble("precProd"));
                producto.setStocProd(json.getDouble("stocProd"));
                em.merge(producto);
                em.getTransaction().commit();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            em.close();
        }
    }

    //Eliminar
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        EntityManager em = getEntityManager();
        int codiProd = Integer.parseInt(request.getParameter("codiProd"));

        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, codiProd);
            if (producto != null) {
                em.remove(producto);
                em.getTransaction().commit();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            em.close();
        }
    }
}
