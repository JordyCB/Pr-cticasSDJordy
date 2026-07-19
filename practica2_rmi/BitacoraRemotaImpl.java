package mx.ipn.esimecu.rpc;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraRemotaImpl extends UnicastRemoteObject implements BitacoraRemota {
    private final String dbUrl;

    public BitacoraRemotaImpl(String dbUrl) throws RemoteException {
        super(0, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        this.dbUrl = dbUrl;
    }

    @Override
    public List<String> consultarHistorial() throws RemoteException {
        List<String> historial = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT timestamp, operacion FROM bitacora ORDER BY timestamp DESC")) {
            
            while (rs.next()) {
                historial.add(rs.getString("timestamp") + " -> " + rs.getString("operacion"));
            }
        } catch (Exception e) {
            throw new RemoteException("Error de BD: " + e.getMessage(), e);
        }
        return historial;
    }
}