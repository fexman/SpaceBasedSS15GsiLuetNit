package Factory;

import MarketEntities.DepotCompany;
import MarketEntities.ISRContainer;
import MarketEntities.XVSM.XvsmDepotCompany;
import MarketEntities.XVSM.XvsmISRContainer;
import Model.Company;
import Model.Investor;
import Service.Broker;
import Service.ConnectionError;
import Util.XvsmUtil;
import org.mozartspaces.core.MzsCoreException;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmFactory implements IFactory {

    private XvsmUtil.XvsmConnection xc;

    public XvsmFactory(String uri) throws ConnectionError {
        try {
            this.xc = XvsmUtil.initConnection(uri);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public ISRContainer newISRContainer() {
        return new XvsmISRContainer();
    }

    @Override
    public AbstractSubscriber newSubscriber(Broker broker) {
        return new XvsmSubscriber(broker);
    }

    @Override
    public DepotCompany newDepotInvestor(Investor investor, String transactionId) throws ConnectionError {
        return null;
    }

    @Override
    public DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionError {
        return new XvsmDepotCompany(comp, transactionId);
    }

    @Override
    public String createTransaction() throws ConnectionError {
        try {
            return XvsmUtil.createTransaction();
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void commitTransaction(String transactionId) throws ConnectionError {
        try {
            XvsmUtil.commitTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void rollbackTransaction(String transactionId) throws ConnectionError {
        try {
            XvsmUtil.rollbackTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void destroy() {
        xc.getCore().shutdown(true);
    }
}
