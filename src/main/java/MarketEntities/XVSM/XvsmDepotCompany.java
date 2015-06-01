package MarketEntities.XVSM;

import MarketEntities.DepotCompany;
import Model.Company;
import Model.Stock;
import Model.TradeObject;
import Service.ConnectionErrorException;
import Util.Container;
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

    public XvsmDepotCompany(Company comp, String transactionId) throws ConnectionErrorException {
        super(comp, transactionId);

        //Setting Depot-name
        this.depotName = Container.DEPOT_COMPANY_TOKEN.toString()+comp.getId();

        xc = XvsmUtil.getXvsmConnection();
        try {
            TransactionReference tx = XvsmUtil.getTransaction(transactionId);
            companyDepot = XvsmUtil.getDepot(comp,tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<Stock> takeStocks(int amount, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        AnyCoordinator.AnySelector selector = AnyCoordinator.newSelector(amount);
        try {
            return xc.getCapi().take(companyDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getTotalAmountOfTradeObjects(String transactionId) throws ConnectionErrorException {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        AnyCoordinator.AnySelector selector = AnyCoordinator.newSelector(MzsConstants.Selecting.COUNT_MAX);
        try {
            return xc.getCapi().read(companyDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx).size();
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void addTradeObjects(List<TradeObject> tradeObjects, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
        for (TradeObject tradeObject : tradeObjects) {
            try {
                xc.getCapi().write(new Entry(tradeObject), companyDepot, XvsmUtil.ACTION_TIMEOUT, tx);
            } catch (MzsCoreException e) {
                throw new ConnectionErrorException(e);
            }
        }
    }
}
