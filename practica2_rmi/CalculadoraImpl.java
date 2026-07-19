package mx.ipn.esimecu.rpc;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CalculadoraImpl extends UnicastRemoteObject implements Calculadora {
    private static final long serialVersionUID = 1L;
    private final String dbUrl;

    public CalculadoraImpl(String dbUrl) throws RemoteException {
        // Asignación de puerto dinámico (0) y sockets SSL
        super(0, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        this.dbUrl = dbUrl;
    }

    private void registrarOperacion(String operacion) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO bitacora (operacion) VALUES (?)")) {
            pstmt.setString(1, operacion);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al guardar en bitácora: " + e.getMessage());
        }
    }

    @Override
    public double sumar(double a, double b) throws RemoteException {
        registrarOperacion("Suma: " + a + " + " + b);
        return a + b;
    }

    @Override
    public double restar(double a, double b) throws RemoteException {
        registrarOperacion("Resta: " + a + " - " + b);
        return a - b;
    }
    
    @Override 
    public double multiplicar(double a, double b) throws RemoteException { 
        registrarOperacion("Multiplicación: " + a + " * " + b);
        return a * b; 
    }
    
    @Override 
    public double dividir(double a, double b) throws RemoteException { 
        if (b == 0.0) throw new RemoteException("División entre cero");
        registrarOperacion("División: " + a + " / " + b);
        return a / b; 
    }

    @Override
    public String quienSoy() throws RemoteException {
        return "Calculadora Segura (SSL/TLS)";
    }
}