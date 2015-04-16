package MarketEntities.XVSM;

import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import Service.ConnectionError;
import Service.Subscribing.TradeOrders.ATradeOrderSubManager;
import Util.XvsmUtil;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.mozartspaces.util.parser.sql.javacc.ParseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmTradeOrdersContainer extends TradeOrderContainer {

    private ContainerReference tradeOrdersContainer;
    private XvsmUtil.XvsmConnection xc;

    public XvsmTradeOrdersContainer() {
        tradeOrdersContainer = XvsmUtil.getContainer(XvsmUtil.Container.TRADE_ORDERS);
        xc = XvsmUtil.getXvsmConnection();
    }


    @Override
    public void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        //Filtering using orderId (should be unique)
        Query query = new Query();
        try {
            query.sql("id = '"+order.getId()+"'");
        } catch (ParseException e) {
            System.out.println("Parse Exception on SQL-query while adding/updating order: "+e.getMessage());
            return;
        }
        Selector selector = QueryCoordinator.newSelector(query, Selector.COUNT_MAX);

        //Removing order using take if already existent, doing nothing if not
        try {
            if (xc.getCapi().take(tradeOrdersContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx).size() > 1) { //Just in case UUID is not unique or query is fucked up! :D
                System.out.println("FATAL ERROR on SQL-query while adding/updating order: Got multiple TradeOrders when selecting with UUID.");
            }
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }


        //Write to traderOrdersContainer
        try {
            xc.getCapi().write(tradeOrdersContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(order));
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }

    }

    @Override
    public List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        //Query building
        Query query = new Query();
        try {
            if (order.getId() != null) { //LOOKING FOR SPECIFIC ORDER WITH ID
                    query.sql("id = '"+order.getId()+"'");
                    System.out.println("WILL QUERY ID");
            }
            if (order.getInvestorId() != null) { //LOOKING FOR INVESTORT/TRADER WITH ID XYZ (COMPANY OR INVESTOR)
                query.sql("investorId = '"+order.getInvestorId()+"'");
                System.out.println("WILL QUERY INVESTORID");
            }
            if (order.getCompanyId() != null) { //LOOKING FOR STOCKS OF COMPANY XYZ
                query.sql("companyId = '"+order.getCompanyId()+"'");
                System.out.println("WILL QUERY COMPANYID");
            }
            if (order.getPriceLimit() != null) {
                System.out.println("WILL QUERY PRICELIMIT");
                switch (order.getType()) {
                    case BUY_ORDER: //LOOKING FOR BUY ORDER, I AM TRYING TO SELL SOMETHING -> PRICE SHOULD BY ABOVE (OR EQUAL TO) MY LIMIT
                        query.sql("priceLimit >= "+order.getPriceLimit());
                        break;
                    case SELL_ORDER: //LOOKING FOR SELL ORDER, I AM TRYING TO BUY SOMETHING -> PRICE SHOULD BY UNDER (OR EQUAL TO) MY LIMIT
                        query.sql("priceLimit <= "+order.getPriceLimit());
                        break;
                    case ANY: //I DONT CARE, SIMPLE MATCHING
                        query.sql("priceLimit = "+order.getPriceLimit());

                }
            }
            switch (order.getStatus()) { //LOOKING FOR ORDERS WITH STATUS ...
                case OPEN: // OPEN
                    query.sql("status = "+TradeOrder.Status.OPEN);
                    System.out.println("WILL QUERY STATUS FOR OPEN");
                    break;
                case PARTIALLY_COMPLETED: // PARTIALLY COMPLETED
                    query.sql("status = "+TradeOrder.Status.PARTIALLY_COMPLETED);
                    System.out.println("WILL QUERY STATUS FOR PARTIALLY COMPLETED");
                    break;
                case NOT_COMPLETED: //OPEN OR PARTIALLY COMPLETED
                    query.sql("status = "+TradeOrder.Status.OPEN+" OR status = "+TradeOrder.Status.PARTIALLY_COMPLETED);
                    System.out.println("WILL QUERY STATUS FOR OPEN OR PARTIALLY COMPLETED");
                    break;
                case COMPLETED: // COMPLETED
                    query.sql("status = "+TradeOrder.Status.COMPLETED);
                    System.out.println("WILL QUERY STATUS FOR COMPLETED");
                    break;
                case DELETED: // DELETED
                    query.sql("status = "+TradeOrder.Status.DELETED);
                    System.out.println("WILL QUERY STATUS FOR DELETED");
                    break;
                case ANY: // I DONT CARE, GIVE ME ALL OF THEM
                    break;
            }
        } catch (ParseException e) {
            System.out.println("Parse Exception on SQL-query while get order by template: " + e.getMessage());
            return new ArrayList<>();
        }
        Selector selector = QueryCoordinator.newSelector(query, Selector.COUNT_MAX);

        //Get by Template
        try {
            return xc.getCapi().read(tradeOrdersContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<TradeOrder> getAllOrders(String transactionId) throws ConnectionError {
        return getOrders(new TradeOrder(),transactionId);
    }

    @Override
    public void subscribe(ATradeOrderSubManager subscriber, String transactionId) throws ConnectionError {
        NotificationManager notificationManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<>();
        operations.add(Operation.WRITE);
        operations.add(Operation.TAKE);

        try {
            notificationManager.createNotification(tradeOrdersContainer, (NotificationListener)subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionError(e);
        }
    }
}
