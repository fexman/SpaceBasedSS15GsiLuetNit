package RMIServer;

import Model.IssueStockRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 23.04.2015.
 */
public interface RmiCallback<T> extends Remote {

        void newData(List<T> newData) throws RemoteException;

}
