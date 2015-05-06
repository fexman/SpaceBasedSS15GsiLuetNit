package RMIServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Felix on 02.05.2015.
 */
public interface ICallbackDummy extends Remote {
    void testConnection() throws RemoteException;
}
