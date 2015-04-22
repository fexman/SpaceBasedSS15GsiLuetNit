package MarketEntities.RMI;

import MarketEntities.DepotCompany;
import Model.Company;
import Model.Stock;
import RMIServer.EntityHandler.IDepotCompanyHandler;
import Service.ConnectionError;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiDepotCompany extends DepotCompany {

    private IDepotCompanyHandler depotCompanyHandler;

    public RmiDepotCompany(Company comp, String transactionId) throws ConnectionError {
        super(comp, transactionId);

        //Setting Depot-name
        this.depotName = comp+"Depot";

        //gethandler
        depotCompanyHandler = RmiUtil.getDepotHandler(comp);
    }

    @Override
    public List<Stock> takeStocks(int amount, String transactionId) throws ConnectionError {
        try {
            return depotCompanyHandler.takeStocks(amount,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws ConnectionError {
        try {
            return depotCompanyHandler.getTotalAmountOfStocks(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void addStocks(List<Stock> stocks, String transactionId) throws ConnectionError {
        try {
            depotCompanyHandler.addStocks(stocks,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }
}
