package servlet;

import dao.KardexJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Kardex;
import dto.Producto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
        while ((line = reader.readLine()) != null) sb.append(line);
        JSONObject json = new JSONObject(sb.toString());

        try {
            Kardex kardex = new Kardex();
            kardex.setCodiKard(json.getInt("codiKard"));
            kardex.setCantProd(json.getInt("cantProd"));
            kardex.setSaldProd(json.getInt("saldProd"));
            kardex.setMoviKard(json.getInt("moviKard"));

            // Relación con Producto
            Producto prod = new Producto();
            prod.setCodiProd(json.getInt("codiProd"));
            kardex.setCodiProd(prod);

            kardexController.create(kardex);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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

        try {
            Integer codiKard = json.getInt("codiKard");
            Kardex kardex = kardexController.findKardex(codiKard);
            if (kardex != null) {
                kardex.setCantProd(json.getInt("cantProd"));
                kardex.setSaldProd(json.getInt("saldProd"));
                kardex.setMoviKard(json.getInt("moviKard"));

                Producto prod = new Producto();
                prod.setCodiProd(json.getInt("codiProd"));
                kardex.setCodiProd(prod);

                kardexController.edit(kardex);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Kardex no encontrado");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // DELETE: Eliminar Kardex
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            Integer codiKard = Integer.parseInt(request.getParameter("codiKard"));
            kardexController.destroy(codiKard);
        } catch (NonexistentEntityException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Kardex no encontrado");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}   

