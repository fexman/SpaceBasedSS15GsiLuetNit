package RMIServer.EntityHandler;

import Model.Stock;
import Service.ConnectionError;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public interface IDepotHandler extends IHandler {

    String getDepotName() throws RemoteException;

    int getTotalAmountOfStocks(String transactionId) throws RemoteException;

    void addStocks(List<Stock> stocks, String transactionId) throws RemoteException;
}
