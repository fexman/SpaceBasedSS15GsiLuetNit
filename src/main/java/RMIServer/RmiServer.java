package RMIServer;

import Model.Company;
import Model.Investor;
import RMIServer.EntityProviders.*;
import RMIServer.EntityProviders.Impl.*;
import Util.RmiUtil;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Felix on 21.04.2015.
 */
public class RmiServer extends Thread implements IRmiServer {

    private int port;
    private Registry registry;
    private HashMap<Company,IDepotCompanyProvider> companyDepots;
    private HashMap<String, IDepotInvestorProvider> investorDepots;
    private IIssueRequestsProvider irContainerProvider;
    private ITradeOrderProvider tradeOrderContainerProvider;
    private IStockPricesProvider stockPricesProvider;
    private ITransactionHistoryProvider transactionHistoryProvider;
    private IBrokerSupportProvider brokerSupportProvider;
    private IFondsIndexProvider fondsIndexProvider;


    public RmiServer(int port) {
        this.port = port;

        irContainerProvider = new IssueRequestsProvider();
        brokerSupportProvider = new BrokerSupportProvider();
        fondsIndexProvider = new FondsIndexProvider();
        tradeOrderContainerProvider = new TradeOrderProvider(brokerSupportProvider);
        stockPricesProvider = new StockPricesProvider(brokerSupportProvider);
        transactionHistoryProvider = new TransactionHistoryProvider();
        companyDepots = new HashMap<>();
        investorDepots = new HashMap<>();
    }

    @Override
    public void run() {

        try {
            //Startup
            registry = LocateRegistry.createRegistry(port);
            IRmiServer remote = (IRmiServer) UnicastRemoteObject.exportObject(this, 0);
            registry.bind(RmiUtil.RMI_SERVER_BINDING, remote);

            //Export providers
            UnicastRemoteObject.exportObject(brokerSupportProvider,0);
            UnicastRemoteObject.exportObject(fondsIndexProvider,0);
            UnicastRemoteObject.exportObject(irContainerProvider, 0);
            UnicastRemoteObject.exportObject(tradeOrderContainerProvider, 0);
            UnicastRemoteObject.exportObject(stockPricesProvider, 0);
            UnicastRemoteObject.exportObject(transactionHistoryProvider, 0);

        } catch (Exception e) {
            System.out.println("Error on startup: "+e.getMessage());
            System.exit(-1);
        }

        System.out.println("StockMarketServer with port "+port+" is up! Enter !exit to shutdown.");
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            String input = scan.next();
            switch (input) {
                case "!exit":
                    try {
                        System.in.close();
                    } catch (IOException e) {
                        //wurscht
                    }
                    shutDown();
                    break;
                default:

            }
        }
    }

    private void shutDown() {
        try {
            //Unexport providers
            UnicastRemoteObject.unexportObject(irContainerProvider, true);
            UnicastRemoteObject.unexportObject(brokerSupportProvider, true);
            UnicastRemoteObject.unexportObject(tradeOrderContainerProvider, true);
            UnicastRemoteObject.unexportObject(stockPricesProvider, true);
            UnicastRemoteObject.unexportObject(transactionHistoryProvider, true);
            UnicastRemoteObject.exportObject(fondsIndexProvider, 0);

            //Unexport depots
            for (IDepotCompanyProvider iDepotCompanyHandler : companyDepots.values()) {
                UnicastRemoteObject.unexportObject(iDepotCompanyHandler, true);
            }

            for (IDepotInvestorProvider investorDepotHandler : investorDepots.values()) {
                UnicastRemoteObject.unexportObject(investorDepotHandler, true);
            }

            //Shutdown
            UnicastRemoteObject.unexportObject(this, true);
            registry.unbind(RmiUtil.RMI_SERVER_BINDING);
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (Exception e) {
            System.out.println("Error on rollbackOpenTransactions: "+e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public IStockPricesProvider getStockPricesContainer() throws RemoteException {
        return stockPricesProvider;
    }

    @Override
    public IBrokerSupportProvider getBrokerSupportContainer() throws RemoteException {
        return brokerSupportProvider;
    }

    @Override
    public IFondsIndexProvider getFondsIndexContainer() throws RemoteException {
        return fondsIndexProvider;
    }


    @Override
    public IIssueRequestsProvider getIssueRequestsContainer() throws RemoteException {
        return irContainerProvider;
    }

    @Override
    public ITradeOrderProvider getTradeOrderContainer() throws RemoteException {
        return tradeOrderContainerProvider;
    }

    @Override
    public ITransactionHistoryProvider getTransactionHistoryContainer() throws RemoteException {
        return transactionHistoryProvider;
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

    @Override
    public IDepotInvestorProvider getDepotInvestor(String investorId) throws RemoteException {

        if (investorDepots.containsKey(investorId)) {
            return investorDepots.get(investorId);
        }

        IDepotInvestorProvider depotInvestorHandler = new DepotInvestorProvider(new Investor(investorId));
        UnicastRemoteObject.exportObject(depotInvestorHandler, 0);
        investorDepots.put(investorId, depotInvestorHandler);

        return depotInvestorHandler;
    }


}
