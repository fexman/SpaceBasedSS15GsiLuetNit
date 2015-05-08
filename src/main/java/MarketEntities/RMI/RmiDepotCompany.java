package MarketEntities.RMI;

import MarketEntities.DepotCompany;
import Model.Company;
import Model.Stock;
import RMIServer.EntityProviders.IDepotCompanyProvider;
import Service.ConnectionErrorException;
import Util.Container;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiDepotCompany extends DepotCompany {

    private IDepotCompanyProvider depotCompany;

    public RmiDepotCompany(Company comp, String transactionId) throws ConnectionErrorException {
        super(comp, transactionId);

        //Setting Depot-name
        this.depotName = Container.DEPOT_COMPANY_TOKEN + comp.getId();

        //get handler
        depotCompany = RmiUtil.getDepot(comp);
    }

    @Override
    public List<Stock> takeStocks(int amount, String transactionId) throws ConnectionErrorException {
        try {
            return depotCompany.takeStocks(amount,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws ConnectionErrorException {
        try {
            return depotCompany.getTotalAmountOfStocks(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void addStocks(List<Stock> stocks, String transactionId) throws ConnectionErrorException {
        try {
            depotCompany.addStocks(stocks,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }
}
