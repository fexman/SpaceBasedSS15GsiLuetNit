package Factory;


import MarketEntities.*;
import MarketEntities.RMI.*;
import MarketEntities.Subscribing.InvestorDepot.AInvestorDepotSubManager;
import MarketEntities.Subscribing.InvestorDepot.IInvestorDepotSub;
import MarketEntities.Subscribing.InvestorDepot.RmiInvestorDepotSubManager;
import MarketEntities.Subscribing.IssueStockRequests.RmiISRSubManager;
import MarketEntities.Subscribing.MarketValues.RmiStockPricesSubManager;
import MarketEntities.Subscribing.TradeOrders.RmiTradeOrderSubManager;
import MarketEntities.Subscribing.TransactionHistory.ATransactionHistorySubManager;
import MarketEntities.Subscribing.TransactionHistory.ITransactionHistorySub;
import MarketEntities.Subscribing.TransactionHistory.RmiTransactionHistorySubManager;
import Model.Company;
import Model.Investor;
import Service.ConnectionError;
import MarketEntities.Subscribing.IssueStockRequests.AISRSubManager;
import MarketEntities.Subscribing.IssueStockRequests.IISRRequestSub;
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

    RmiUtil.RmiConnection rc;

    public RmiFactory(String uri) throws ConnectionError {
        rc = RmiUtil.initConnection(uri);
    }

    @Override
    public ISRContainer newISRContainer() {
        return new RmiISRContainer();
    }

    @Override
    public TradeOrderContainer newTradeOrdersContainer() {
        return new RmiTradeOrderContainer();
    }

    @Override
    public StockPricesContainer newStockPricesContainer() {
        return new RmiStockPricesContainer();
    }

    @Override
    public TransactionHistoryContainer newTransactionHistoryContainer() {
        return new RmiTransactionHistoryContainer();
    }

    @Override
    public AISRSubManager newIssueStockRequestSubManager(IISRRequestSub subscription) {
        try {
            return new RmiISRSubManager(subscription);
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
    public DepotInvestor newDepotInvestor(Investor investor, String transactionId) throws ConnectionError {
        return new RmiDepotInvestor(investor, transactionId);
    }

    @Override
    public DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionError {
        return new RmiDepotCompany(comp, transactionId);
    }

    @Override
    public String createTransaction(TransactionTimeout timeout) throws ConnectionError {
        return null;
    }

    @Override
    public void commitTransaction(String transactionId) throws ConnectionError {

    }

    @Override
    public void rollbackTransaction(String transactionId) throws ConnectionError {

    }

    @Override
    public void destroy() {
        System.exit(0);
    }

    private void remoteExceptionThrown() {
        System.out.println("FATAL ERROR GOT REMOTE EXCEPTION: SHUTTING DOWN.");
        destroy();
    }
}
