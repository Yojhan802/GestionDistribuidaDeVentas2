package servlet;
import dto.Cliente;
import dto.Detalle;
import dto.Producto;
import dto.Venta;
import java.io.IOException;
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

@WebServlet(name = "VentaRegistrarServlet", urlPatterns = {"/venta-registrar"})
public class VentaRegistrarServlet extends HttpServlet {
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
            List<Venta> ventas = em.createNamedQuery("Venta.findAll", Venta.class).getResultList();
            JSONArray jsonArray = new JSONArray();

            for (Venta v : ventas) {
                JSONObject obj = new JSONObject();
                obj.put("codiVent", v.getCodiVent());
                obj.put("fechVent", v.getFechVent());
                obj.put("codiClie", v.getCodiClie().getCodiClie());
                obj.put("nombClie", v.getCodiClie().getNombClie());
                // Puedes añadir más info si quieres
                jsonArray.put(obj);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(jsonArray.toString());

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
            JSONObject jsonRequest = new JSONObject(sb.toString());

            // Ejemplo: {
            //   "codiClie": 1,
            //   "fechVent": "2025-05-18",
            //   "detalles": [
            //       {"codiProd": 10, "cantidad": 2, "precio": 15.5},
            //       {"codiProd": 12, "cantidad": 1, "precio": 25.0}
            //    ]
            // }

            Integer codiClie = jsonRequest.getInt("codiClie");
            String fechVent = jsonRequest.getString("fechVent");
            JSONArray detalles = jsonRequest.getJSONArray("detalles");

            // Obtener nuevo código para venta
            Integer codiVent = obtenerSiguienteCodigoVenta(em);

            Cliente cliente = em.find(Cliente.class, codiClie);
            if (cliente == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"error\":\"Cliente no encontrado\"}");
                return;
            }

            Venta venta = new Venta();
            venta.setCodiVent(codiVent);
            venta.setFechVent(fechVent);
            venta.setCodiClie(cliente);

            em.getTransaction().begin();
            em.persist(venta);

            // Insertar detalles
            for (int i = 0; i < detalles.length(); i++) {
                JSONObject det = detalles.getJSONObject(i);
                Integer codiProd = det.getInt("codiProd");
                Integer cantidad = det.getInt("cantidad");
                Double precio = det.getDouble("precio");

                Producto producto = em.find(Producto.class, codiProd);
                if (producto == null) {
                    em.getTransaction().rollback();
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print("{\"error\":\"Producto no encontrado\"}");
                    return;
                }

                Detalle detalle = new Detalle();
                detalle.setCodiVent(venta);
                detalle.setCodiProd(producto);
                detalle.setCantDeta(cantidad);
                detalle.setPrecProd(precio);

                em.persist(detalle);

                // Opcional: actualizar stock producto
                producto.setStocProd(producto.getStocProd() - cantidad);
                em.merge(producto);
            }

            em.getTransaction().commit();

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print("{\"mensaje\":\"Venta registrada con éxito\",\"codiVent\":" + codiVent + "}");

        } catch (Exception e) {
            em.getTransaction().rollback();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            em.close();
        }
    }

    private int obtenerSiguienteCodigoVenta(EntityManager em) {
        Integer maxCodigo = (Integer) em.createQuery("SELECT MAX(v.codiVent) FROM Venta v").getSingleResult();
        return (maxCodigo == null ? 1 : maxCodigo + 1);
    }
}
