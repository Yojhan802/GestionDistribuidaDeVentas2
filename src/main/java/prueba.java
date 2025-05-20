
import conexion.Conexion;
import dao.KardexJpaController;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author yojha
 */
public class prueba {
    public static void main(String[] args) {
        if (Conexion.getConnection() != null) {
            System.out.println("✅ La conexión fue exitosa.");
        } else {
            System.out.println("❌ No se pudo establecer la conexión.");
        }
    }
}
