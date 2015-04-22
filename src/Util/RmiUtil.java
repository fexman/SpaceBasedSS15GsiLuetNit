package Util;

import Model.Company;
import RMIServer.EntityHandler.IDepotCompanyHandler;
import RMIServer.EntityHandler.IHandler;
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

    private static HashMap<XvsmUtil.Container, IHandler> handlers = new HashMap<>();
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

        //Adding handlers
        try {
            handlers.put(XvsmUtil.Container.ISSUED_STOCK_REQUESTS, rc.getRmiServer().getIssueStockRequestContainer());
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
        return rc;
    }

    public static IHandler getHandler(XvsmUtil.Container cont) {
        return handlers.get(cont);
    }

    public static IDepotCompanyHandler getDepotHandler(Company company) throws ConnectionError {
        try {
            return rc.getRmiServer().getDepotCompanyHandler(company);
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
