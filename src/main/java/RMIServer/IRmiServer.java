package RMIServer;


import Model.Company;
import RMIServer.EntityProviders.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Felix on 21.04.2015.
 */
public interface IRmiServer extends Remote {

    IStockPricesProvider getStockPricesContainer() throws RemoteException;

    IBrokerSupportProvider getBrokerSupportProvider() throws RemoteException;

    IISRContainerProvider getIssueStockRequestContainer() throws RemoteException;

    ITradeOrderProvider getTradeOrderContainer() throws RemoteException;

    ITransactionHistoryProvider getTransactionHistoryContainer() throws RemoteException;

    IDepotCompanyProvider getDepotCompany(Company company) throws RemoteException;

    IDepotInvestorProvider getDepotInvestor(String investorId) throws RemoteException;
}
