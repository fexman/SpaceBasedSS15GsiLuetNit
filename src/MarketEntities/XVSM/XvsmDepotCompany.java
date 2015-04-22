package MarketEntities.XVSM;

import MarketEntities.DepotCompany;
import Model.Company;
import Model.Stock;
import Service.ConnectionError;
import Util.XvsmUtil;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.core.*;

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

        AnyCoordinator.AnySelector selector = AnyCoordinator.newSelector(amount);
        try {
            return xc.getCapi().take(companyDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        AnyCoordinator.AnySelector selector = AnyCoordinator.newSelector(MzsConstants.Selecting.COUNT_MAX);
        try {
            return xc.getCapi().read(companyDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx).size();
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
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
