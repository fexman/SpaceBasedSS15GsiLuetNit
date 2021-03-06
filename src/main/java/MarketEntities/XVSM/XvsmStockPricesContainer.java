package MarketEntities.XVSM;

import MarketEntities.StockPricesContainer;
import MarketEntities.Subscribing.ASubManager;
import Model.Company;
import Model.MarketValue;
import Service.ConnectionErrorException;
import Util.Container;
import Util.XvsmUtil;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 20.04.2015.
 */
public class XvsmStockPricesContainer  extends StockPricesContainer {

    private ContainerReference stockPricesContainer;
    private XvsmUtil.XvsmConnection xc;
    private XvsmUtil util;

    public XvsmStockPricesContainer(XvsmUtil util) {
        this.util = util;
        stockPricesContainer = util.getContainer(Container.STOCK_PRICES);
        xc = util.getXvsmConnection();
    }


    @Override
    public void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        Selector selector = KeyCoordinator.newSelector(marketValue.getId(), Selector.COUNT_MAX);
        CoordinationData coordData = KeyCoordinator.newCoordinationData(marketValue.getId());

        //Write to traderOrdersContainer
        try {
            xc.getCapi().delete(stockPricesContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
            xc.getCapi().write(new Entry(marketValue, coordData), stockPricesContainer, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public MarketValue getMarketValue(String id, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        Selector selector = KeyCoordinator.newSelector(id, Selector.COUNT_MAX);
        List<MarketValue> result;

        try {
            result = xc.getCapi().read(stockPricesContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }

        if (result.size() == 1) {
            return result.get(0);
        }

        return null;
    }

    @Override
    public List<MarketValue> getAll(String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        Selector selector = FifoCoordinator.newSelector(Selector.COUNT_MAX);

        try {
            return xc.getCapi().read(stockPricesContainer,selector,XvsmUtil.ACTION_TIMEOUT,tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<MarketValue> getFonds(String transactionId) throws ConnectionErrorException {

        TransactionReference tx = util.getTransaction(transactionId);

        Query query = new Query();

        Property isCompany = Property.forName("isCompany");
        query.filter(isCompany.equalTo(false));

        Selector selector = QueryCoordinator.newSelector(query, Selector.COUNT_MAX);

        try {
            return xc.getCapi().read(stockPricesContainer,selector,XvsmUtil.ACTION_TIMEOUT,tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<MarketValue> getCompanies(String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        Query query = new Query();

        Property isCompany = Property.forName("isCompany");
        query.filter(isCompany.equalTo(true));

        Selector selector = QueryCoordinator.newSelector(query, Selector.COUNT_MAX);

        try {
            return xc.getCapi().read(stockPricesContainer,selector,XvsmUtil.ACTION_TIMEOUT,tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
        NotificationManager notificationManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<>();
        operations.add(Operation.WRITE);

        try {
            notificationManager.createNotification(stockPricesContainer, (NotificationListener)subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionErrorException(e);
        }
    }
}
