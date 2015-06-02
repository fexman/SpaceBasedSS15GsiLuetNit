package Util;

import Model.Company;
import Model.MarketValue;
import Model.TradeOrder;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.aspects.ContainerIPoint;
import org.mozartspaces.core.requests.WriteEntriesRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Felix on 05.04.2015.
 */

public class XvsmUtil {

    public static final long ACTION_TIMEOUT = 5000l;
    public static final long INFINITE_TAKE = MzsConstants.RequestTimeout.INFINITE;

    private static HashMap<String, TransactionReference> transactions = new HashMap<>();
    private static HashMap<Container, ContainerReference> containers = new HashMap<>();
    private static XvsmConnection xc;

    /**
     * Looks up container from space with given capi and uri, or creates new one if not existent.
     *
     * @param containerName
     * @param space
     * @param capi
     * @return
     * @throws MzsCoreException Thrown if container could neither be looked up, nor created.
     */
    private static ContainerReference lookUpOrCreateContainer(String containerName, URI space, Capi capi, TransactionReference tx, List<CoordinatorType> types) throws MzsCoreException {
        ContainerReference cref;
        try {
            cref = capi.lookupContainer(containerName, space, MzsConstants.RequestTimeout.DEFAULT, tx);
        } catch (MzsCoreException e) {
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            for (CoordinatorType ct : types) {
                obligatoryCoords.add(CoordinatorType.getCoordinator(ct));
            }
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, tx);
        }
        return cref;
    }


    public static void rollbackOpenTransactions() {
        if (xc != null) {
            for (TransactionReference tx : transactions.values()) {
                try {
                    xc.getCapi().rollbackTransaction(tx);
                } catch (MzsCoreException e) {
                    System.out.println("What happend to: "+tx+"?");
                }
            }
        }
    }

    /**
     * Connects to a XVSM-Space and returns XvsmConnection.
     *
     * @param spaceUri Uri of the XVSM-Space
     * @return XvsmConnection containing capi and space Uri
     */
    public static XvsmConnection initConnection(String spaceUri, boolean withSpace) throws MzsCoreException {

        if (xc != null) {
            xc.getCore().shutdown(false);
        }

        xc = new XvsmConnection(spaceUri, withSpace);
        System.out.println("XvsmUtil: Connection initialized - XVSM up and running.");

        //Crea
        // te "hardcoded" containers
        containers.put(Container.ISSUED_REQUESTS, lookUpOrCreateContainer(Container.ISSUED_REQUESTS.toString(), xc.getSpace(), xc.getCapi(), null,
                new ArrayList<CoordinatorType>() {{
                    add(CoordinatorType.FIFO_COORDINATOR);
                }}));
        containers.put(Container.TRANSACTION_HISTORY, lookUpOrCreateContainer(Container.TRANSACTION_HISTORY.toString(), xc.getSpace(), xc.getCapi(), null,
                new ArrayList<CoordinatorType>() {{
                    add(CoordinatorType.FIFO_COORDINATOR);
                }}));
        containers.put(Container.TRADE_ORDERS, lookUpOrCreateContainer(Container.TRADE_ORDERS.toString(), xc.getSpace(), xc.getCapi(), null,
                new ArrayList<CoordinatorType>() {{
                    add(CoordinatorType.QUERY_COORDINATOR);
                }}));
        containers.put(Container.STOCK_PRICES, lookUpOrCreateContainer(Container.STOCK_PRICES.toString(), xc.getSpace(), xc.getCapi(), null,
                new ArrayList<CoordinatorType>() {{
                    add(CoordinatorType.KEY_COORDINATOR);
                    add(CoordinatorType.FIFO_COORDINATOR);
                    add(CoordinatorType.QUERY_COORDINATOR);
                }}));

        containers.put(Container.BROKER_SPSUPPORT, lookUpOrCreateContainer(Container.BROKER_SPSUPPORT.toString(), xc.getSpace(), xc.getCapi(), null,
                new ArrayList<CoordinatorType>() {{
                    add(CoordinatorType.FIFO_COORDINATOR);
                }}));

        containers.put(Container.BROKER_TOSUPPORT, lookUpOrCreateContainer(Container.BROKER_TOSUPPORT.toString(), xc.getSpace(), xc.getCapi(), null,
                new ArrayList<CoordinatorType>() {{
                    add(CoordinatorType.FIFO_COORDINATOR);
                }}));

        //Server only
        if (withSpace) {
            xc.getCapi().addContainerAspect(new BrokerStockPricesAspect(), containers.get(Container.STOCK_PRICES), ContainerIPoint.POST_WRITE);
            xc.getCapi().addContainerAspect(new BrokerTradeOrdersAspect(), containers.get(Container.TRADE_ORDERS), ContainerIPoint.POST_WRITE);
        }
        return xc;
    }

    /**
     * Returns "hardcoded" container-instances, like ISSUED_STOCK_REQUESTS.
     * For "dynamic" container-instances like Company and Investor-Depots use see getDepot(Company)/getDepot(Investor)
     *
     * @param cont
     * @return
     */
    public static ContainerReference getContainer(Container cont) {
        return containers.get(cont);
    }

    /**
     * Creates or looks up a company-stock-depot
     *
     * @param company
     * @return
     * @throws MzsCoreException
     */
    public static ContainerReference getDepot(Company company, TransactionReference tx) throws MzsCoreException {
        return lookUpOrCreateContainer("DEPOT_COMPANY_" + company.getId(), xc.getSpace(), xc.getCapi(), tx, new ArrayList<CoordinatorType>());
    }

    /**
     * Creates or looks up an investor-stock-depot
     *
     * @param investorId
     * @return
     * @throws MzsCoreException
     */
    public static ContainerReference getDepot(String investorId, TransactionReference tx) throws MzsCoreException {
        List<CoordinatorType> coordinatorTypes = new ArrayList<>();
        coordinatorTypes.add(CoordinatorType.LABEL_COORDINATOR);
        coordinatorTypes.add(CoordinatorType.TYPE_COORDINATOR);
        return lookUpOrCreateContainer("DEPOT_INVESTOR_" + investorId, xc.getSpace(), xc.getCapi(), tx, coordinatorTypes);
    }

    public static String createTransaction(TransactionTimeout timeout) throws MzsCoreException {

        long timeoutValue = getTransactionTimeoutValue(timeout);

        TransactionReference tx = xc.getCapi().createTransaction(timeoutValue, xc.getSpace());
        UUID transactionId = UUID.randomUUID();
        transactions.put(transactionId.toString(), tx);

        System.out.println("CREATED TRANSACTION: "+transactionId.toString()+" / "+tx);
        return transactionId.toString();
    }

    public static TransactionReference getTransaction(String transactionId) {
        return transactions.get(transactionId);
    }

    public static void commitTransaction(String transactionId) throws MzsCoreException {
        System.out.println("COMMITING TRANSACTION: "+transactionId+" / "+transactions.get(transactionId));
        xc.getCapi().commitTransaction(getTransaction(transactionId));
        removeTransaction(transactionId);
    }

    public static void rollbackTransaction(String transactionId) throws MzsCoreException {
        System.out.println("ROLLBACKING TRANSACTION: "+transactionId+" / "+transactions.get(transactionId));
        xc.getCapi().rollbackTransaction(getTransaction(transactionId));
        removeTransaction(transactionId);
    }

    public static void removeTransaction(String transactionId) {
        transactions.remove(transactionId);
    }

    private static long getTransactionTimeoutValue(TransactionTimeout timeout) {
        switch (timeout) {
            case INFINITE:
                return MzsConstants.RequestTimeout.INFINITE;
                //return MzsConstants.TransactionTimeout.INFINITE;
            case TRY_ONCE:
                return MzsConstants.RequestTimeout.ZERO;
            default:
                return ACTION_TIMEOUT;
        }
    }

    /**
     * Returns the current connection to a XVSM-Space, may return null if no connection was estalbished yet.
     *
     * @return
     */
    public static XvsmConnection getXvsmConnection() {
        return xc;
    }

    public static class XvsmConnection {

        private URI space;
        private Capi capi;
        private MzsCore core;

        public XvsmConnection(String spaceUri, boolean withSpace) {
            this.space = URI.create(spaceUri);
            if (withSpace) {
                this.core = DefaultMzsCore.newInstance(this.space.getPort());
            } else {
                this.core = DefaultMzsCore.newInstance(0);
            }
            this.capi = new Capi(core);
        }

        public MzsCore getCore() {
            return core;
        }

        public URI getSpace() {
            return space;
        }

        public Capi getCapi() {
            return capi;
        }
    }

    public enum CoordinatorType {

        ANY_COORDINATOR,
        RANDOM_COORDINATOR,
        FIFO_COORDINATOR,
        LIFO_COORDINATOR,
        KEY_COORDINATOR,
        LABEL_COORDINATOR,
        QUERY_COORDINATOR,
        TYPE_COORDINATOR;

        public static Coordinator getCoordinator(CoordinatorType type) {
            switch (type) {
                case ANY_COORDINATOR:
                    return new AnyCoordinator();
                case RANDOM_COORDINATOR:
                    return new RandomCoordinator();
                case FIFO_COORDINATOR:
                    return new FifoCoordinator();
                case LIFO_COORDINATOR:
                    return new LifoCoordinator();
                case KEY_COORDINATOR:
                    return new KeyCoordinator();
                case LABEL_COORDINATOR:
                    return new LabelCoordinator();
                case QUERY_COORDINATOR:
                    return new QueryCoordinator();
                case TYPE_COORDINATOR:
                    return new TypeCoordinator();
                default:
                    return new AnyCoordinator();
            }
        }
    }

    private static class BrokerStockPricesAspect extends AbstractContainerAspect {

        @Override
        public AspectResult postWrite(WriteEntriesRequest request,
                                      Transaction tx, SubTransaction stx, Capi3AspectPort capi3,
                                      int executionCount) {
            try {
                for (Entry e: request.getEntries()) {
                    MarketValue mw = (MarketValue)e.getValue();
                    if (mw.isPriceChanged()) {
                        xc.getCapi().write(new Entry(e.getValue()), containers.get(Container.BROKER_SPSUPPORT), ACTION_TIMEOUT, new TransactionReference(tx.getId(), xc.getSpace()));
                    }
                }
            } catch (MzsCoreException e) {
                e.printStackTrace();
                return AspectResult.SKIP;
            }
            System.out.println("Broker SPAspect executed."+request.getEntries());
            return AspectResult.OK;
        }
    }

    private static class BrokerTradeOrdersAspect extends AbstractContainerAspect {

        public AspectResult postWrite(WriteEntriesRequest request,
                                      Transaction tx, SubTransaction stx, Capi3AspectPort capi3,
                                      int executionCount) {
            try {
                for (Entry e : request.getEntries()) {
                    TradeOrder to = (TradeOrder)e.getValue();
                    if (to.getJustChanged()) {
                        xc.getCapi().write(e, containers.get(Container.BROKER_TOSUPPORT), ACTION_TIMEOUT, new TransactionReference(tx.getId(), xc.getSpace()));
                        System.out.println("Broker TOAspect executed for: " + e.getValue());
                    }
                }
            } catch (MzsCoreException e) {
                e.printStackTrace();
                return AspectResult.SKIP;
            }
            return AspectResult.OK;
        }
    }

}