package MarketEntities.XVSM;

import MarketEntities.DepotCompany;
import Model.Company;
import Model.Stock;
import Service.ConnectionError;
import Util.XvsmUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public class XvsmDepotCompany extends DepotCompany {

    private ContainerReference companyDepot;
    private XvsmUtil.XvsmConnection xc;

    public XvsmDepotCompany(Company comp, String transactionId) throws ConnectionError {
        super(comp, transactionId);

        //Setting Depot-name
        this.depotName = XvsmUtil.Container.DEPOT_COMPANY_TOKEN.toString()+comp.getId();

        xc = XvsmUtil.getXvsmConnection();
        try {
            TransactionReference tx = XvsmUtil.getTransaction(transactionId);
            companyDepot = XvsmUtil.getDepot(comp,tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<Stock> takeStocks(int amount, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
        return null;
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
        return 0;
    }

    @Override
    public void addStocks(List<Stock> stocks, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
        for (Stock s : stocks) {
            try {
                xc.getCapi().write(companyDepot, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(s));
            } catch (MzsCoreException e) {
                throw new ConnectionError(e);
            }
        }
    }
}
