package RMIServer;


import Model.Company;
import RMIServer.EntityHandler.IDepotCompanyHandler;
import RMIServer.EntityHandler.IIssueStockRequestContainerHandler;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Felix on 21.04.2015.
 */
public interface IRmiServer extends Remote {

    IIssueStockRequestContainerHandler getIssueStockRequestContainer() throws RemoteException;

    IDepotCompanyHandler getDepotCompanyHandler(Company company) throws RemoteException;
}
