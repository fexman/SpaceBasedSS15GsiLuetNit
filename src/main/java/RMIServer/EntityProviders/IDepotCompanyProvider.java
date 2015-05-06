package RMIServer.EntityProviders;

import Model.Company;
import Model.Stock;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public interface IDepotCompanyProvider extends IDepotProvider {

    Company getCompany() throws RemoteException;

    List<Stock> takeStocks(int amount, String transactionId) throws RemoteException;
}
