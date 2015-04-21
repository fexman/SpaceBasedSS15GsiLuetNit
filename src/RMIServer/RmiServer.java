package RMIServer;

import RMIServer.EntityHandler.IIssueStockRequestContainerHandler;
import RMIServer.EntityHandler.IssueStockRequestContainerHandler;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Felix on 21.04.2015.
 */
public class RmiServer extends Thread implements IRmiServer {

    private int port;
    private Registry registry;
    private static final String RMI_BINDING_NAME = "StockMarketServer";

    private IIssueStockRequestContainerHandler isrContainerHandler;

    public RmiServer(int port) {
        this.port = port;

        isrContainerHandler = new IssueStockRequestContainerHandler();
    }



    @Override
    public void run() {

        try {
            registry = LocateRegistry.createRegistry(port);
            IRmiServer remote = (IRmiServer) UnicastRemoteObject.exportObject(this, 0);
            registry.bind(RMI_BINDING_NAME, remote);
        } catch (Exception e) {
            System.out.println("Error on startup: "+e.getMessage());
            System.exit(-1);
        }

        System.out.println("StockMarketServer is up! Hit <ENTER> at any time to exit!");

        try {
            System.in.read();
        } catch (IOException e) {
           //This wont happen
        }

        try {
            UnicastRemoteObject.unexportObject(this, true);
            registry.unbind(RMI_BINDING_NAME);
            UnicastRemoteObject.unexportObject(registry,true);
        } catch (Exception e) {
            System.out.println("Error on shutdown: "+e.getMessage());
            System.exit(-1);
        }
    }


    @Override
    public IIssueStockRequestContainerHandler getIssueStockRequestContainer() {
        return isrContainerHandler;
    }
}
