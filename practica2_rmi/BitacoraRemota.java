package mx.ipn.esimecu.rpc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BitacoraRemota extends Remote {
    List<String> consultarHistorial() throws RemoteException;
}