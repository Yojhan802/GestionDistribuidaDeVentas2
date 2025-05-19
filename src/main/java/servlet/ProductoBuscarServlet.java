package servlet;
import dto.Producto;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(name = "ProductoBuscarServlet", urlPatterns = {"/producto-buscar"})
public class ProductoBuscarServlet extends HttpServlet {
    @PersistenceUnit(unitName = "TuUnidadPersistencia")
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("TuUnidadPersistencia");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String codiProdStr = request.getParameter("codiProd");
        EntityManager em = getEntityManager();
        try {
            if (codiProdStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Integer codiProd = Integer.parseInt(codiProdStr);
            Producto producto = em.find(Producto.class, codiProd);
            if (producto == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            JSONObject json = new JSONObject();
            json.put("codiProd", producto.getCodiProd());
            json.put("nombProd", producto.getNombProd());
            json.put("precProd", producto.getPrecProd());
            json.put("stocProd", producto.getStocProd());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(json.toString());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            em.close();
        }
    }
}
