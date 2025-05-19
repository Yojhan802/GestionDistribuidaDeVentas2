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
 * 
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/producto"})
public class ProductoServlet extends HttpServlet {
    
    //Colocar la persistencia del proyecto
    @PersistenceUnit(unitName = "com.mycompany_TPD06_war_1.0-SNAPSHOTPU")
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");

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

        
        EntityManager em = getEntityManager();
    try {
        String codiProdParam = request.getParameter("codiProd");

        // Si envían el código por parámetro, buscar solo ese producto
        if (codiProdParam != null && !codiProdParam.trim().isEmpty()) {
            try {
                int codiProd = Integer.parseInt(codiProdParam);
                Producto producto = em.find(Producto.class, codiProd);

                if (producto != null) {
                    JSONObject obj = new JSONObject();
                    obj.put("codiProd", producto.getCodiProd());
                    obj.put("nombProd", producto.getNombProd());
                    obj.put("precProd", producto.getPrecProd());
                    obj.put("stocProd", producto.getStocProd());

                    PrintWriter out = response.getWriter();
                    out.print(obj.toString());
                    out.flush();
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Código inválido");
            }
        } else {
            // Si no hay parámetro, devuelve todos los productos
            List<Producto> productos = em.createNamedQuery("Producto.findAll", Producto.class).getResultList();
            
            JSONArray jsonArray = new JSONArray();
            
            for (Producto p : productos) {
                JSONObject obj = new JSONObject();
                obj.put("codiProd", p.getCodiProd());
                obj.put("nombProd", p.getNombProd());
                obj.put("precProd", p.getPrecProd());
                obj.put("stocProd", p.getStocProd());
                jsonArray.put(obj);
            }
            
            PrintWriter out = response.getWriter();
            out.print(jsonArray.toString());
            
            out.flush();
        }
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

    JSONObject json = new JSONObject(jsonBuilder.toString());

    try {
        // Si el JSON contiene la acción "delete", procesamos eliminación
        if (json.has("action") && "delete".equalsIgnoreCase(json.getString("action"))) {
            int codiProd = json.getInt("codiProd");

            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, codiProd);
            if (producto == null) {
                em.getTransaction().rollback();
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                return;
            }
            em.remove(producto);
            em.getTransaction().commit();

            // Respuesta con el producto eliminado
            response.getWriter().write(producto.toString());
            return;
        }

        // Si no es eliminación, asumimos creación o actualización
        Producto producto;
        if (json.has("codiProd")) {
            // Actualización (PUT)
            int codiProd = json.getInt("codiProd");
            producto = em.find(Producto.class, codiProd);
            if (producto == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                return;
            }
        } else {
            // Creación (POST)
            producto = new Producto();
            int nuevoCodigo = em.createQuery("SELECT COALESCE(MAX(p.codiProd), 0) FROM Producto p", Integer.class)
                               .getSingleResult() + 1;
            producto.setCodiProd(nuevoCodigo);
        }

        // Setear campos comunes para creación y actualización
        producto.setNombProd(json.getString("nombProd"));
        producto.setPrecProd(json.getDouble("precProd"));
        producto.setStocProd(json.getDouble("stocProd"));

        em.getTransaction().begin();
        if (!em.contains(producto)) {
            em.persist(producto); // Para creación
        } else {
            em.merge(producto);   // Para actualización
        }
        em.getTransaction().commit();

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(producto.toString());

    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
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

    String accion = json.optString("accion", "crear");  // por defecto crear

    try {
        em.getTransaction().begin();

        if ("eliminar".equalsIgnoreCase(accion)) {
            // Acción eliminar
            int codiProd = json.getInt("codiProd");
            Producto producto = em.find(Producto.class, codiProd);
            if (producto == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                em.getTransaction().rollback();
                return;
            }
            em.remove(producto);
            em.getTransaction().commit();

            // Retorna JSON con el producto eliminado (solo código para confirmar)
            JSONObject resp = new JSONObject();
            resp.put("codiProd", codiProd);
            response.getWriter().write(resp.toString());

        } else if ("editar".equalsIgnoreCase(accion)) {
            // Acción editar
            int codiProd = json.getInt("codiProd");
            Producto producto = em.find(Producto.class, codiProd);
            if (producto == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado");
                em.getTransaction().rollback();
                return;
            }
            producto.setNombProd(json.getString("nombProd"));
            producto.setPrecProd(json.getDouble("precProd"));
            producto.setStocProd(json.getDouble("stocProd"));
            em.merge(producto);
            em.getTransaction().commit();

            response.getWriter().write(json.toString());

        } else {
            // Acción crear (por defecto)
            Producto producto = new Producto();
            int nuevoCodigo = em.createQuery("SELECT COALESCE(MAX(p.codiProd), 0) FROM Producto p", Integer.class).getSingleResult() + 1;
            producto.setCodiProd(nuevoCodigo);
            producto.setNombProd(json.getString("nombProd"));
            producto.setPrecProd(json.getDouble("precProd"));
            producto.setStocProd(json.getDouble("stocProd"));

            em.persist(producto);
            em.getTransaction().commit();

            // Devuelve JSON con producto creado y su código nuevo
            JSONObject resp = new JSONObject();
            resp.put("codiProd", nuevoCodigo);
            resp.put("nombProd", producto.getNombProd());
            resp.put("precProd", producto.getPrecProd());
            resp.put("stocProd", producto.getStocProd());

            response.getWriter().write(resp.toString());
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

        String codiParam = null;
        String queryString = request.getQueryString();
        if (queryString != null) {
        for (String param : queryString.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals("codiProd")) {
                codiParam = java.net.URLDecoder.decode(pair[1], "UTF-8");
                break;
            }
        }
    }

        if (codiParam == null || codiParam.trim().isEmpty()) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta parámetro codiProd");
        return;
    }

        int codiProd;
        try {
        codiProd = Integer.parseInt(codiParam);
        } catch (NumberFormatException e) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Código inválido");
        return;
        }

        EntityManager em = getEntityManager();
        try {
        em.getTransaction().begin();
        Producto producto = em.find(Producto.class, codiProd);
        if (producto != null) {
            em.remove(producto);
            em.getTransaction().commit();

            JSONObject obj = new JSONObject();
            obj.put("status", "eliminado");
            obj.put("codiProd", codiProd);

            PrintWriter out = response.getWriter();
            out.print(obj.toString());
            out.flush();

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
