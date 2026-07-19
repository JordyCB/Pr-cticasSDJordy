package mx.ipn.esimecu.rpc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.util.List;

public class ClienteRMI {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "192.168.56.2";
        
        // 1. Conectar al registro RMI indicando el protocolo SSL
        Registry registry = LocateRegistry.getRegistry(host, 1099, new SslRMIClientSocketFactory());

        // 2. Descargar stubs
        Calculadora calc = (Calculadora) registry.lookup("CalculadoraIPN");
        BitacoraRemota bitacora = (BitacoraRemota) registry.lookup("BitacoraIPN");

        // 3. Ejecución de procedimientos remotos
        System.out.println("Conectado a: " + calc.quienSoy());
        System.out.println("3 + 4 = " + calc.sumar(3, 4));
        System.out.println("10 - 6 = " + calc.restar(10, 6));

        // 4. Consulta a la base de datos distribuida
        System.out.println("\n--- Historial de Operaciones en BD Remota ---");
        List<String> hist = bitacora.consultarHistorial();
        for (String log : hist) {
            System.out.println(log);
        }
    }
}