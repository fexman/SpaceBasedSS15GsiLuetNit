package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import Service.ConnectionErrorException;
import Util.Container;
import Util.XvsmUtil;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
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
        tradeOrdersContainer = XvsmUtil.getContainer(Container.TRADE_ORDERS);
        xc = XvsmUtil.getXvsmConnection();
    }


    @Override
    public void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionErrorException {
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
            ArrayList<TradeOrder> e = xc.getCapi().take(tradeOrdersContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
            if (xc.getCapi().take(tradeOrdersContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx).size() > 1) { //Just in case UUID is not unique or query is fucked up! :D
                System.out.println("FATAL ERROR on SQL-query while adding/updating order: Got multiple TradeOrders when selecting with UUID.");
            }
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }


        //Write to traderOrdersContainer
        try {
            xc.getCapi().write(new Entry(order), tradeOrdersContainer, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }

    }

    @Override
    public List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        //Query building
        Query query = new Query();
        System.out.print("[XvsmTradeOrdersContainer] WILL QUERY FOR:");
        try {
            if (order.getId() != null) { //LOOKING FOR SPECIFIC ORDER WITH ID
                query.sql("id = '"+order.getId()+"'");
                System.out.print(" ID");
            }
            if (order.getInvestorId() != null) { //LOOKING FOR INVESTORT/TRADER WITH ID XYZ (COMPANY OR INVESTOR)
                query.sql("investorId = '"+order.getInvestorId()+"'");
                System.out.print(" INVESTORID");
            }
            if (order.getCompanyId() != null) { //LOOKING FOR STOCKS OF COMPANY XYZ
                query.sql("companyId = '"+order.getCompanyId()+"'");
                System.out.print(" COMPANYID");
            }
            if (order.getPriceLimit() != null) {
                System.out.print(" PRICELIMIT");
                switch (order.getType()) {
                    case BUY_ORDER: //LOOKING FOR BUY ORDER, INFINITE AM TRYING TO SELL SOMETHING -> PRICE SHOULD BY ABOVE (OR EQUAL TO) MY LIMIT
                        query.sql("priceLimit >= "+order.getPriceLimit()); // search for price bigger than/equal to min price of sell order
                        break;
                    case SELL_ORDER: //LOOKING FOR SELL ORDER, INFINITE AM TRYING TO BUY SOMETHING -> PRICE SHOULD BY UNDER (OR EQUAL TO) MY LIMIT
                        query.sql("priceLimit <= "+order.getPriceLimit()); // search for price smaller than/equal to max price of buy order
                        break;
                    case ANY: //INFINITE DONT CARE, SIMPLE MATCHING
                        query.sql("priceLimit = "+order.getPriceLimit());
                }
            }

            if (!order.getType().equals(TradeOrder.Type.ANY)) {
                Property type = Property.forName("type");
                query.filter(type.equalTo(order.getType()));
                System.out.print(" TYPE=" + order.getType());
            }

            if (order.isPrioritized() != null) {
                Property prioritized = Property.forName("prioritized");
                query.filter(prioritized.equalTo(order.isPrioritized()));
                System.out.print(" PRIORITIZED");
            }

            Property status = Property.forName("status");
            switch (order.getStatus()) { //LOOKING FOR ORDERS WITH STATUS ...
                case OPEN: // OPEN
                    query.filter(status.equalTo(TradeOrder.Status.OPEN));
                    System.out.print(" STATUS=OPEN");
                    break;
                case PARTIALLY_COMPLETED: // PARTIALLY COMPLETED
                    query.filter(status.equalTo(TradeOrder.Status.PARTIALLY_COMPLETED));
                    System.out.print(" STATUS=PARTIALLY_COMPLETED");
                    break;
                case NOT_COMPLETED: //OPEN OR PARTIALLY COMPLETED
                    Matchmaker statusOpen = status.equalTo(TradeOrder.Status.OPEN);
                    Matchmaker statusPartiallyCompleted = status.equalTo(TradeOrder.Status.PARTIALLY_COMPLETED);
                    query.filter(Matchmakers.or(statusOpen, statusPartiallyCompleted));
                    System.out.print(" STATUS=NOT_COMPLETED");
                    break;
                case COMPLETED: // COMPLETED
                    query.filter(status.equalTo(TradeOrder.Status.COMPLETED));
                    System.out.print(" STATUS=COMPLETED");
                    break;
                case DELETED: // DELETED
                    query.filter(status.equalTo(TradeOrder.Status.DELETED));
                    System.out.print(" STATUS=DELETED");
                    break;
                case NOT_DELETED: //EVERYTHING EXCEPT DELETED
                    query.filter(status.allNotEqualTo(TradeOrder.Status.DELETED));
                    System.out.print(" STATUS=NOT_DELETED");
                case ANY: // INFINITE DONT CARE, GIVE ME ALL OF THEM
                    break;
            }
            System.out.print("\n");
        } catch (ParseException e) {
            System.out.println("Parse Exception on SQL-query while get order by template: " + e.getMessage());
            return new ArrayList<>();
        }
        Selector selector = QueryCoordinator.newSelector(query, Selector.COUNT_MAX);

        //Get by Template
        try {
            return xc.getCapi().read(tradeOrdersContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public TradeOrder takeOrder(TradeOrder tradeOrder, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
        Query query = new Query();
        try {
            query.sql("id = '" + tradeOrder.getId() + "'");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Selector selector = QueryCoordinator.newSelector(query, Selector.COUNT_MAX);

        try {
            List<TradeOrder> tradeOrders = xc.getCapi().take(tradeOrdersContainer, selector, MzsConstants.RequestTimeout.TRY_ONCE, tx);
            if (tradeOrders.size() > 0) {
                return tradeOrders.get(0);
            }
            return null;
        } catch (MzsCoreException e) {
            // trade order already taken
            return null;
        }
    }

    @Override
    public List<TradeOrder> getAllOrders(String transactionId) throws ConnectionErrorException {
        return getOrders(new TradeOrder(),transactionId);
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
        NotificationManager notificationManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<>();
        operations.add(Operation.WRITE);

        try {
            notificationManager.createNotification(tradeOrdersContainer, (NotificationListener)subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionErrorException(e);
        }
    }
}
