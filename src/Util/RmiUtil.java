package Util;

import Model.Company;
import Model.Investor;
import RMIServer.EntityProviders.IDepotCompanyProvider;
import RMIServer.EntityProviders.IDepotInvestorProvider;
import RMIServer.EntityProviders.IProvider;
import RMIServer.IRmiServer;

import Service.ConnectionError;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiUtil {

    private static RmiConnection rc;

    private static HashMap<Container, IProvider> providers = new HashMap<>();
    public static final String RMI_SERVER_BINDING = "MarketServer";

    public static RmiConnection initConnection(String uri) throws ConnectionError {
        String[] uriSplit = uri.split(":");
        if (uriSplit.length != 2) {
            throw new ConnectionError("Could not parse uri: Invalid format");
        }
        int port = 0;
        try {
            port = Integer.parseInt(uriSplit[1]);
        } catch (NumberFormatException e) {
            throw new ConnectionError("Invalid port: "+uriSplit[1]+" is not a valid integer-number!");
        }

        rc = new RmiConnection(uriSplit[0],port);

        //Adding providers
        try {
            providers.put(Container.ISSUED_STOCK_REQUESTS, rc.getRmiServer().getIssueStockRequestContainer());
            providers.put(Container.TRADE_ORDERS, rc.getRmiServer().getTradeOrderContainer());
            providers.put(Container.STOCK_PRICES, rc.getRmiServer().getStockPricesContainer());
            providers.put(Container.TRANSACTION_HISTORY, rc.getRmiServer().getTransactionHistoryContainer());
            providers.put(Container.BROKER_TOSUPPORT, rc.getRmiServer().getBrokerSupportProvider());
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
        return rc;
    }

    public static IProvider getContainer(Container cont) {
        return providers.get(cont);
    }

    public static IDepotCompanyProvider getDepot(Company company) throws ConnectionError {
        try {
            return rc.getRmiServer().getDepotCompany(company);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    public static IDepotInvestorProvider getDepot(String investorId) throws ConnectionError {
        try {
            return rc.getRmiServer().getDepotInvestor(investorId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    public static RmiConnection getRmiConnection() {
        return rc;
    }

    public static class RmiConnection {

        private Registry registry;
        private IRmiServer rmiServer;

        public RmiConnection(String address, int port) throws ConnectionError {
            try {
                registry = LocateRegistry.getRegistry(address, port);
                rmiServer = (IRmiServer) registry.lookup(RMI_SERVER_BINDING);
            } catch (Exception e) {
                throw new ConnectionError(e);
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
