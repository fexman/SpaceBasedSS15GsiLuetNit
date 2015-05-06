package RMIServer.EntityProviders;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.Investor;
import Model.Stock;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by j0h1 on 02.05.2015.
 */
public interface IDepotInvestorProvider extends IDepotProvider {

    Investor getInvestor() throws RemoteException;

    double getBudget(String transactionId) throws RemoteException;

    void setBudget(double amount, String transactionId) throws RemoteException;

    List<Stock> takeStocks(Company comp, int amount, String transactionId) throws RemoteException;

    int getStockAmount(String stockName, String transactionId) throws RemoteException;

    List<Stock> readAllStocks(String transactionId) throws RemoteException;

    void subscribe(IRmiCallback<Serializable> callback) throws RemoteException;

    void unsubscribe(IRmiCallback<Serializable> callback) throws RemoteException;
}
