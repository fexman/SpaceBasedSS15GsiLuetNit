package RMIServer;

import Model.Company;
import RMIServer.EntityProviders.*;
import RMIServer.EntityProviders.Impl.DepotCompanyProvider;
import RMIServer.EntityProviders.Impl.ISRContainerProvider;
import RMIServer.EntityProviders.Impl.StockPricesProvider;
import RMIServer.EntityProviders.Impl.TradeOrderProvider;
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
    private HashMap<Company,IDepotCompanyProvider> companyDepots;
    private IISRContainerProvider isrContainerProvider;
    private ITradeOrderProvider tradeOrderContainerProvider;
    private IStockPricesProvider stockPricesProvider;

    public RmiServer(int port) {
        this.port = port;

        isrContainerProvider = new ISRContainerProvider();
        tradeOrderContainerProvider = new TradeOrderProvider();
        stockPricesProvider = new StockPricesProvider();
        companyDepots = new HashMap<>();

    }

    @Override
    public void run() {

        try {
            //Startup
            registry = LocateRegistry.createRegistry(port);
            IRmiServer remote = (IRmiServer) UnicastRemoteObject.exportObject(this, 0);
            registry.bind(RmiUtil.RMI_SERVER_BINDING, remote);

            //Export providers
            UnicastRemoteObject.exportObject(isrContainerProvider, 0);
            UnicastRemoteObject.exportObject(tradeOrderContainerProvider, 0);
            UnicastRemoteObject.exportObject(stockPricesProvider, 0);

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

            //Unexport providers
            UnicastRemoteObject.unexportObject(isrContainerProvider,true);
            UnicastRemoteObject.unexportObject(tradeOrderContainerProvider,true);
            UnicastRemoteObject.unexportObject(stockPricesProvider,true);

            //Unexport depots
            for (IDepotCompanyProvider iDepotCompanyHandler : companyDepots.values()) {
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
    public IStockPricesProvider getStockPricesContainer() throws RemoteException {
        return stockPricesProvider;
    }

    @Override
    public IISRContainerProvider getIssueStockRequestContainer() throws RemoteException {
        return isrContainerProvider;
    }

    @Override
    public ITradeOrderProvider getTradeOrderContainer() throws RemoteException {
        return tradeOrderContainerProvider;
    }

    public IDepotCompanyProvider getDepotCompany(Company company) throws RemoteException{

        //Depot already existing?
        if (companyDepots.containsKey(company)) {
            return companyDepots.get(company);
        }

        //Create and export new depot
        IDepotCompanyProvider depotCompanyHandler = new DepotCompanyProvider(company);
        UnicastRemoteObject.exportObject(depotCompanyHandler, 0);
        companyDepots.put(company, depotCompanyHandler);

        //Return new depot
        return depotCompanyHandler;

    }
}
