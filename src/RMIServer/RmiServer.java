package RMIServer;

import Model.Company;
import RMIServer.EntityHandler.*;
import Util.RmiUtil;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * Created by Felix on 21.04.2015.
 */
public class RmiServer extends Thread implements IRmiServer {

    private int port;
    private Registry registry;
    private HashMap<Company,IDepotCompanyHandler> companyDepots;
    private IIssueStockRequestContainerHandler isrContainerHandler;
    private ITradeOrderContainerHandler tradeOrderContainerHandler;

    public RmiServer(int port) {
        this.port = port;

        isrContainerHandler = new IssueStockRequestContainerHandler();
        tradeOrderContainerHandler = new TradeOrderContainerHandler();
        companyDepots = new HashMap<>();

    }

    @Override
    public void run() {

        try {
            //Startup
            registry = LocateRegistry.createRegistry(port);
            IRmiServer remote = (IRmiServer) UnicastRemoteObject.exportObject(this, 0);
            registry.bind(RmiUtil.RMI_SERVER_BINDING, remote);

            //Export handlers
            UnicastRemoteObject.exportObject(isrContainerHandler, 0);
            UnicastRemoteObject.exportObject(tradeOrderContainerHandler, 0);

        } catch (Exception e) {
            System.out.println("Error on startup: "+e.getMessage());
            System.exit(-1);
        }

        System.out.println("StockMarketServer with port "+port+" is up! Hit <ENTER> at any time to exit!");
        try {
            System.in.read();
        } catch (IOException e) {
           //This wont happen
        }

        try {

            //Unexport handlers
            UnicastRemoteObject.unexportObject(isrContainerHandler,true);
            UnicastRemoteObject.unexportObject(tradeOrderContainerHandler,true);

            //Unexport depots
            for (IDepotCompanyHandler iDepotCompanyHandler : companyDepots.values()) {
                UnicastRemoteObject.unexportObject(iDepotCompanyHandler, true);
            }

            //Shutdown
            UnicastRemoteObject.unexportObject(this, true);
            registry.unbind(RmiUtil.RMI_SERVER_BINDING);
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

    @Override
    public ITradeOrderContainerHandler getTradeOrderContainer() {
        return tradeOrderContainerHandler;
    }

    public IDepotCompanyHandler getDepotCompanyHandler (Company company) throws RemoteException{

        //Depot already existing?
        if (companyDepots.containsKey(company)) {
            return companyDepots.get(company);
        }

        //Create and export new depot
        IDepotCompanyHandler depotCompanyHandler = new DepotCompanyHandler(company);
        UnicastRemoteObject.exportObject(depotCompanyHandler, 0);
        companyDepots.put(company, depotCompanyHandler);

        //Return new depot
        return depotCompanyHandler;

    }
}
