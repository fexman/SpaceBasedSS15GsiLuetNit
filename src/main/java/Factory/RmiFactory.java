package Factory;


import MarketEntities.*;
import MarketEntities.RMI.*;
import MarketEntities.Subscribing.InvestorDepot.AInvestorDepotSubManager;
import MarketEntities.Subscribing.InvestorDepot.IInvestorDepotSub;
import MarketEntities.Subscribing.InvestorDepot.RmiInvestorDepotSubManager;
import MarketEntities.Subscribing.IssueRequests.RmiIssueRequestSubManager;
import MarketEntities.Subscribing.MarketValues.RmiStockPricesSubManager;
import MarketEntities.Subscribing.TradeOrders.RmiTradeOrderSubManager;
import MarketEntities.Subscribing.TransactionHistory.ATransactionHistorySubManager;
import MarketEntities.Subscribing.TransactionHistory.ITransactionHistorySub;
import MarketEntities.Subscribing.TransactionHistory.RmiTransactionHistorySubManager;
import Model.Company;
import Model.Investor;
import Service.ConnectionErrorException;
import MarketEntities.Subscribing.IssueRequests.AIssueRequestSubManager;
import MarketEntities.Subscribing.IssueRequests.IIssueRequestSub;
import MarketEntities.Subscribing.MarketValues.AStockPricesSubManager;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.TradeOrders.ATradeOrderSubManager;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import Util.RmiUtil;
import Util.TransactionTimeout;

import java.rmi.RemoteException;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiFactory implements IFactory {

    private RmiUtil util;

    public RmiFactory(String uri) throws ConnectionErrorException {
        util = new RmiUtil(uri);
    }

    @Override
    public IssueRequestContainer newIssueRequestContainer() {
        return new RmiIssueRequestContainer(util);
    }

    @Override
    public TradeOrderContainer newTradeOrdersContainer() {
        return new RmiTradeOrderContainer(util);
    }

    @Override
    public StockPricesContainer newStockPricesContainer() {
        return new RmiStockPricesContainer(util);
    }

    @Override
    public TransactionHistoryContainer newTransactionHistoryContainer() {
        return new RmiTransactionHistoryContainer(util);
    }

    @Override
    public BrokerSupportContainer newBrokerSupportContainer() {
        return new RmiBrokerSupportContainer(util);
    }

    @Override
    public FondsIndexContainer newFondsIndexContainer() {
        return new RmiFondsIndexContainer(util);
    }

    @Override
    public AIssueRequestSubManager newIssueRequestSubManager(IIssueRequestSub subscription) {
        try {
            return new RmiIssueRequestSubManager(subscription);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ATradeOrderSubManager newTradeOrderSubManager(ITradeOrderSub subscription) {
        try {
            return new RmiTradeOrderSubManager(subscription);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AStockPricesSubManager newStockPricesSubManager(IStockPricesSub subscription) {
        try {
            return new RmiStockPricesSubManager(subscription);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ATransactionHistorySubManager newTransactionHistorySubManager(ITransactionHistorySub subscription) {
        try {
            return new RmiTransactionHistorySubManager(subscription);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AInvestorDepotSubManager newInvestorDepotSubManager(IInvestorDepotSub subscription) {
        try {
            return new RmiInvestorDepotSubManager(subscription);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DepotInvestor newDepotInvestor(Investor investor, String transactionId) throws ConnectionErrorException {
        return new RmiDepotInvestor(util, investor, transactionId);
    }

    @Override
    public DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionErrorException {
        return new RmiDepotCompany(util, comp, transactionId);
    }

    @Override
    public String createTransaction(TransactionTimeout timeout) throws ConnectionErrorException {
        return null;
    }

    @Override
    public void removeTransaction(String transactionId) {

    }

    @Override
    public void commitTransaction(String transactionId) throws ConnectionErrorException {

    }

    @Override
    public void rollbackTransaction(String transactionId) throws ConnectionErrorException {

    }

    @Override
    public void destroy() {
        System.exit(0);
    }

    @Override
    public String getProtocolString() {
        return "RMI";
    }

    private void remoteExceptionThrown() {
        System.out.println("FATAL ERROR GOT REMOTE EXCEPTION: SHUTTING DOWN.");
        destroy();
    }
}
