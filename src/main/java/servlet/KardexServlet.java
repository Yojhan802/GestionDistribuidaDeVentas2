package servlet;

import dao.KardexJpaController;
import dao.ProductoJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Kardex;
import dto.Producto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "KardexServlet", urlPatterns = {"/kardex"})
public class KardexServlet extends HttpServlet {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    private final KardexJpaController kardexController = new KardexJpaController(emf);
    private final ProductoJpaController productoController = new ProductoJpaController();

    // GET: Listar Kardex
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            List<Kardex> kardexList = kardexController.findKardexEntities();
            JSONArray jsonArray = new JSONArray();
            for (Kardex k : kardexList) {
                JSONObject obj = new JSONObject();
                obj.put("codiKard", k.getCodiKard());
                obj.put("cantProd", k.getCantProd());
                obj.put("saldProd", k.getSaldProd());
                obj.put("moviKard", k.getMoviKard());
                obj.put("codiProd", k.getCodiProd().getCodiProd()); // Relación
                obj.put("nombProd", k.getCodiProd().getNombProd()); // Nombre producto relacionado
                jsonArray.put(obj);
            }
            out.print(jsonArray.toString());
            out.flush();
        }
    }

    // POST: Crear Kardex
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = request.getReader();
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null){
        sb.append(line);
    }
    JSONObject json = new JSONObject(sb.toString());

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_TPD06_war_1.0-SNAPSHOTPU");
    EntityManager em = emf.createEntityManager();

    try{
        
        Kardex kardex = new Kardex();
        
        kardex.setCantProd(json.getInt("cantProd"));
        kardex.setSaldProd(json.getInt("saldProd"));
        kardex.setMoviKard(json.getInt("moviKard"));

        // Relación con Producto
        int codiProd = json.getInt("codiProd");
        Producto prod = em.find(Producto.class,codiProd);
        if (prod == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"El codigo de producto no existe");
            return;
        }
        kardex.setCodiProd(prod);

        kardexController.create(kardex);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }catch(Exception e){
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }finally{
        em.close();
    }
}
    
    // PUT: Modificar Kardex
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = request.getReader();
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) sb.append(line);
    JSONObject json = new JSONObject(sb.toString());

    
    EntityManager em = emf.createEntityManager();

    try {
        int codiProd = json.getInt("codiProd");

        // Buscar el Kardex que corresponde a ese producto (asumo uno por producto)
        Kardex kardex = null;
        List<Kardex> kardexList = kardexController.findKardexEntities();
        for (Kardex k : kardexList) {
            if (k.getCodiProd().getCodiProd() == codiProd) {
                kardex = k;
                break;
            }
        }

        if (kardex != null) {
            kardex.setCantProd(json.getInt("cantProd"));
            kardex.setSaldProd(json.getInt("saldProd"));
            kardex.setMoviKard(json.getInt("moviKard"));

            
            Producto prod = em.find(Producto.class, codiProd);
            if (prod == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El código de producto no existe");
                return;
            }
            kardex.setCodiProd(prod);

            kardexController.edit(kardex);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Kardex no encontrado para el producto dado");
        }
    } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    } finally {
        em.close();
    }
}

    // DELETE: Eliminar Kardex
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
        int codiProd = Integer.parseInt(request.getParameter("codiProd"));

        // Buscar el Kardex por código producto
        Kardex kardex = null;
        List<Kardex> kardexList = kardexController.findKardexEntities();
        for (Kardex k : kardexList) {
            if (k.getCodiProd().getCodiProd() == codiProd) {
                kardex = k;
                break;
            }
        }

        if (kardex != null) {
            kardexController.destroy(kardex.getCodiKard());
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Kardex no encontrado para el producto dado");
        }
    } catch (NonexistentEntityException e) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Kardex no encontrado");
    } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
}

