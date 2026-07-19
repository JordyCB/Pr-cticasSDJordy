package mx.ipn.esimecu.rpc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ServidorRMI {
    public static void main(String[] args) throws Exception {
        String dbUrl = "jdbc:sqlite:rmi_lab.db";
        
        // 1. Inicialización de la base de datos (Persistencia)
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS bitacora (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "operacion TEXT, " +
                         "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
        }

        // 2. Creación del registro RMI seguro usando SSL
        Registry registry = LocateRegistry.createRegistry(1099, 
                new SslRMIClientSocketFactory(), 
                new SslRMIServerSocketFactory());

        // 3. Enlace de los objetos remotos al rmiregistry
        registry.rebind("CalculadoraIPN", new CalculadoraImpl(dbUrl));
        registry.rebind("BitacoraIPN", new BitacoraRemotaImpl(dbUrl));

        System.out.println("Servidor RMI (SSL + SQLite) inicializado y en escucha.");
    }
}