
package conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion { 

    // Cambia estos datos por los de tu hosting
private static final String URL = "jdbc:mysql://15.204.212.48/gestiond_distribuido";

    private static final String USER = "gestiond_yojhan";
    private static final String PASSWORD = "Yojhan1106$";

    public static Connection getConnection() {
        try {
            // Cargar el driver MySQL (puede no ser necesario en versiones nuevas)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Intentar conectar
            Connection conn = DriverManager.getConnection(URL, "gestiond_yojhan", "Yojhan1106$");
            System.out.println("✅ Conexión exitosa a la base de datos");
            return conn;

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: Driver MySQL no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos.");
            e.printStackTrace();
        }

        return null;
    }
    
}


