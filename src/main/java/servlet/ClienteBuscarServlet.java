package servlet;
import dao.ClienteJpaController;
import dto.Cliente;
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

@WebServlet(name = "ClienteBuscarServlet", urlPatterns = {"/clientebuscar"})
public class ClienteBuscarServlet extends HttpServlet {

    ClienteJpaController clie = new ClienteJpaController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String codiClieStr = request.getParameter("codiClie");
        
 try{
     if (codiClieStr == null || codiClieStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        Cliente cliente = clie.findCliente(Integer.parseInt(codiClieStr));

            if (cliente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            JSONObject json = new JSONObject();
            json.put("codiClie", cliente.getCodiClie());
            json.put("nombClie", cliente.getNombClie());
            // añade otros campos que necesites
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(json.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } 
    }
}
