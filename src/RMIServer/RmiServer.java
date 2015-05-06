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
    private IISRContainerProvider isrContainerProvider;
    private ITradeOrderProvider tradeOrderContainerProvider;
    private IStockPricesProvider stockPricesProvider;
    private ITransactionHistoryProvider transactionHistoryProvider;
    private IBrokerSupportProvider brokerSupportProvider;


    public RmiServer(int port) {
        this.port = port;

        isrContainerProvider = new ISRContainerProvider();
        brokerSupportProvider = new BrokerSupportProvider();
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
            UnicastRemoteObject.exportObject(isrContainerProvider, 0);
            UnicastRemoteObject.exportObject(tradeOrderContainerProvider, 0);
            UnicastRemoteObject.exportObject(stockPricesProvider, 0);
            UnicastRemoteObject.exportObject(transactionHistoryProvider, 0);

        } catch (Exception e) {
            System.out.println("Error on startup: "+e.getMessage());
            System.exit(-1);
        }

        System.out.println("StockMarketServer with port "+port+" is up! Enter !exit to shutdown, !help for help.");
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            String input = scan.next();
            switch (input) {
                case "!spinfo":
                    System.out.println(stockPricesProvider.toString());
                    break;
                case "!toinfo":
                    System.out.println(tradeOrderContainerProvider.toString());
                    break;
                case "!isrinfo":
                    System.out.println(isrContainerProvider.toString());
                    break;
                case "!thinfo":
                    System.out.println(transactionHistoryProvider.toString());
                case "!depots_c":
                    String info = "======== COMPANY DEPOTS ========\n";
                    int counter = 1;
                    for (IDepotCompanyProvider dcp : companyDepots.values()) {
                        info += "["+counter+"]: "+dcp.toString()+"\n";
                        counter++;
                    }
                    info += "================================\n";
                    System.out.println(info);
                    break;
                case "!exit":
                    try {
                        System.in.close();
                    } catch (IOException e) {
                        //wurscht
                    }
                    shutDown();
                    break;
                case "!help":
                    System.out.print("Available commands:\n\t!exit\t\trollbackOpenTransactions server\n\t!help\t\tcommand info\n\n\t!isrinfo\tisr container provider info\n" +
                            "\t!toinfo\t\ttrade order container provider info\n" +
                            "\t!spinfo\t\tstock prices container provider info\n\n\t!depots_c\tcompany depots info\n\t!depots_i\tinvestor depots info\n");
                    break;
                default:
                    System.out.println("Unkown command.");
            }
        }
    }

    private void shutDown() {
        try {
            //Unexport providers
            UnicastRemoteObject.unexportObject(isrContainerProvider, true);
            UnicastRemoteObject.unexportObject(brokerSupportProvider, true);
            UnicastRemoteObject.unexportObject(tradeOrderContainerProvider, true);
            UnicastRemoteObject.unexportObject(stockPricesProvider, true);
            UnicastRemoteObject.unexportObject(transactionHistoryProvider, true);

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
    public IBrokerSupportProvider getBrokerSupportProvider() throws RemoteException {
        return brokerSupportProvider;
    }


    @Override
    public IISRContainerProvider getIssueStockRequestContainer() throws RemoteException {
        return isrContainerProvider;
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
