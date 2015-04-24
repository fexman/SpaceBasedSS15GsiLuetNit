package Util;

import Model.Company;
import Model.Investor;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Felix on 05.04.2015.
 */

public class XvsmUtil {

    public static final int ACTION_TIMEOUT = 5000;

    private static HashMap<String, TransactionReference> transactions = new HashMap<>();
    private static HashMap<Container, ContainerReference> containers = new HashMap<>();
    private static XvsmConnection xc;

    /**
     * Looks up container from space with given capi and uri, or creates new one if not existent.
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
            for (CoordinatorType ct: types) {
                obligatoryCoords.add(CoordinatorType.getCoordinator(ct));
            }
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, tx);
        }
        return cref;
    }


    /**
     * Connects to a XVSM-Space and returns XvsmConnection.
     * @param spaceUri Uri of the XVSM-Space
     * @return XvsmConnection containing capi and space Uri
     */
    public static XvsmConnection initConnection(String spaceUri) throws MzsCoreException {

        if (xc != null) {
            xc.getCore().shutdown(false);
        }

        xc = new XvsmConnection(spaceUri);
        System.out.println("XvsmUtil: Connection initialized - XVSM up and running.");

        //Create "hardcoded" containers
        containers.put(Container.ISSUED_STOCK_REQUESTS, lookUpOrCreateContainer(Container.ISSUED_STOCK_REQUESTS.toString(), xc.getSpace(), xc.getCapi(),null,
                new ArrayList<CoordinatorType>() {{ add(CoordinatorType.FIFO_COORDINATOR); }}));
        containers.put(Container.TRANSACTION_HISTORY, lookUpOrCreateContainer(Container.TRANSACTION_HISTORY.toString(), xc.getSpace(), xc.getCapi(),null,  new ArrayList<CoordinatorType>()));
        containers.put(Container.TRADE_ORDERS, lookUpOrCreateContainer(Container.TRADE_ORDERS.toString(), xc.getSpace(), xc.getCapi(),null,
                new ArrayList<CoordinatorType>() {{ add(CoordinatorType.QUERY_COORDINATOR); }}));
        containers.put(Container.STOCK_PRICES, lookUpOrCreateContainer(Container.STOCK_PRICES.toString(), xc.getSpace(), xc.getCapi(), null,
                new ArrayList<CoordinatorType>() {{
                    add(CoordinatorType.KEY_COORDINATOR); add(CoordinatorType.FIFO_COORDINATOR);
                }}));

        return xc;
    }

    /**
     * Returns "hardcoded" container-instances, like ISSUED_STOCK_REQUESTS.
     * For "dynamic" container-instances like Company and Investor-Depots use see getDepot(Company)/getDepot(Investor)
     * @param cont
     * @return
     */
    public static ContainerReference getContainer(Container cont) {
        return containers.get(cont);
    }

    /**
     * Creates or looks up a company-stock-depot
     * @param company
     * @return
     * @throws MzsCoreException
     */
    public static ContainerReference getDepot(Company company, TransactionReference tx) throws MzsCoreException {
        return lookUpOrCreateContainer("DEPOT_COMPANY_" + company.getId(), xc.getSpace(), xc.getCapi(), tx, new ArrayList<CoordinatorType>());
    }

    /**
     * Creates or looks up an investor-stock-depot
     * @param investor
     * @return
     * @throws MzsCoreException
     */
    public static ContainerReference getDepot(Investor investor, TransactionReference tx) throws MzsCoreException {
        return lookUpOrCreateContainer("DEPOT_INVESTOR_" + investor.getId(), xc.getSpace(), xc.getCapi(), tx, new ArrayList<CoordinatorType>() {{ add(CoordinatorType.LABEL_COORDINATOR); }});
    }

    public static String createTransaction() throws MzsCoreException {
        TransactionReference tx = xc.getCapi().createTransaction(ACTION_TIMEOUT, xc.getSpace());
        UUID transactionId = UUID.randomUUID();
        transactions.put(transactionId.toString(), tx);
        return transactionId.toString();
    }

    public static TransactionReference getTransaction(String transactionId) {
        return transactions.get(transactionId);
    }

    public static void deleteTransaction(String transactionId) {
        transactions.remove(transactionId);
    }

    public static void commitTransaction(String transactionId) throws MzsCoreException {
        xc.getCapi().commitTransaction(getTransaction(transactionId));
        deleteTransaction(transactionId);
    }

    public static void rollbackTransaction(String transactionId) throws MzsCoreException {
        xc.getCapi().rollbackTransaction(getTransaction(transactionId));
        deleteTransaction(transactionId);
    }

    /**
     * Returns the current connection to a XVSM-Space, may return null if no connection was estalbished yet.
     * @return
     */
    public static XvsmConnection getXvsmConnection () {
        return xc;
    }

    public static class XvsmConnection {

        private URI space;
        private Capi capi;
        private MzsCore core;

        public XvsmConnection(String spaceUri) {
            this.space = URI.create(spaceUri);
            this.core = DefaultMzsCore.newInstanceWithoutSpace();
            this.capi = new Capi(core);
        }

        public MzsCore getCore() { return core; }

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
        QUERY_COORDINATOR;

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
                default:
                    return new AnyCoordinator();
            }
        }
    }
}
