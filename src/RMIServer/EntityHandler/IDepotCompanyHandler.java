package RMIServer.EntityHandler;

import Model.Company;
import Model.Stock;
import Service.ConnectionError;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public interface IDepotCompanyHandler extends IDepotHandler {

    public Company getCompany() throws RemoteException;

    public List<Stock> takeStocks(int amount, String transactionId) throws RemoteException;
}
