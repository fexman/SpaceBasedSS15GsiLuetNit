package Util;

import Model.Company;
import RMIServer.EntityProviders.IDepotCompanyProvider;
import RMIServer.EntityProviders.IDepotInvestorProvider;
import RMIServer.EntityProviders.IProvider;
import RMIServer.IRmiServer;

import Service.ConnectionErrorException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiUtil {

    public static final String RMI_SERVER_BINDING = "MarketServer";

    private RmiConnection rc;
    private HashMap<Container, IProvider> providers = new HashMap<>();

    public RmiUtil(String uri) throws ConnectionErrorException {
        String[] uriSplit = uri.split(":");
        if (uriSplit.length != 2) {
            throw new ConnectionErrorException("Could not parse uri: Invalid format");
        }
        int port = 0;
        try {
            port = Integer.parseInt(uriSplit[1]);
        } catch (NumberFormatException e) {
            throw new ConnectionErrorException("Invalid port: "+uriSplit[1]+" is not a valid integer-number!");
        }

        rc = new RmiConnection(uriSplit[0],port);

        //Adding providers
        try {
            providers.put(Container.ISSUED_REQUESTS, rc.getRmiServer().getIssueRequestsContainer());
            providers.put(Container.TRADE_ORDERS, rc.getRmiServer().getTradeOrderContainer());
            providers.put(Container.STOCK_PRICES, rc.getRmiServer().getStockPricesContainer());
            providers.put(Container.TRANSACTION_HISTORY, rc.getRmiServer().getTransactionHistoryContainer());
            providers.put(Container.BROKER_TOSUPPORT, rc.getRmiServer().getBrokerSupportContainer());
            providers.put(Container.FONDS_INDEX_CONTAINER, rc.getRmiServer().getFondsIndexContainer());
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    public IProvider getContainer(Container cont) {
        return providers.get(cont);
    }

    public IDepotCompanyProvider getDepot(Company company) throws ConnectionErrorException {
        try {
            return rc.getRmiServer().getDepotCompany(company);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    public IDepotInvestorProvider getDepot(String investorId) throws ConnectionErrorException {
        try {
            return rc.getRmiServer().getDepotInvestor(investorId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    public RmiConnection getRmiConnection() {
        return rc;
    }

    public static class RmiConnection {

        private Registry registry;
        private IRmiServer rmiServer;

        public RmiConnection(String address, int port) throws ConnectionErrorException {
            try {
                registry = LocateRegistry.getRegistry(address, port);
                rmiServer = (IRmiServer) registry.lookup(RMI_SERVER_BINDING);
            } catch (Exception e) {
                throw new ConnectionErrorException(e);
            }
        }

        public Registry getRegistry() {
            return registry;
        }

        public IRmiServer getRmiServer() {
            return rmiServer;
        }
    }
}
