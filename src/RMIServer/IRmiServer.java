package RMIServer;


import Model.Company;
import RMIServer.EntityProviders.IDepotCompanyProvider;
import RMIServer.EntityProviders.IISRContainerProvider;
import RMIServer.EntityProviders.IStockPricesProvider;
import RMIServer.EntityProviders.ITradeOrderProvider;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Felix on 21.04.2015.
 */
public interface IRmiServer extends Remote {

    IStockPricesProvider getStockPricesContainer() throws RemoteException;

    IISRContainerProvider getIssueStockRequestContainer() throws RemoteException;

    ITradeOrderProvider getTradeOrderContainer() throws RemoteException;

    IDepotCompanyProvider getDepotCompany(Company company) throws RemoteException;
}
