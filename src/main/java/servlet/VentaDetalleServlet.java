/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/VentaServlet")
public class VentaDetalleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String codiVent = request.getParameter("codiVent");
        String codiClie = request.getParameter("codiClie");
        String fechVen = request.getParameter("fechVen");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Venta Registrada</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; padding: 20px; background-color: #f7f7f7; }");
        out.println("table { border-collapse: collapse; width: 50%; margin: auto; background: white; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
        out.println("th, td { padding: 12px; border: 1px solid #ccc; text-align: left; }");
        out.println("th { background-color: #ff6f61; color: white; }");
        out.println("</style></head><body>");
        out.println("<h2 style='text-align:center;'>Detalles de la Venta</h2>");
        out.println("<table>");
        out.println("<tr><th>Campo</th><th>Valor</th></tr>");
        out.println("<tr><td>Código Venta</td><td>" + codiVent + "</td></tr>");
        out.println("<tr><td>Código Cliente</td><td>" + codiClie + "</td></tr>");
        out.println("<tr><td>Fecha Venta</td><td>" + fechVen + "</td></tr>");
        out.println("</table>");
        out.println("<br><div style='text-align:center'><a href='index.html'>← Volver</a></div>");
        out.println("</body></html>");
    }
}
