package Factory;

import MarketEntities.*;
import MarketEntities.Subscribing.InvestorDepot.AInvestorDepotSubManager;
import MarketEntities.Subscribing.InvestorDepot.IInvestorDepotSub;
import MarketEntities.Subscribing.InvestorDepot.XvsmInvestorDepotSubManager;
import MarketEntities.Subscribing.TransactionHistory.ATransactionHistorySubManager;
import MarketEntities.Subscribing.TransactionHistory.ITransactionHistorySub;
import MarketEntities.Subscribing.TransactionHistory.XvsmTransactionHistorySubManager;
import MarketEntities.XVSM.*;
import Model.Company;
import Model.Investor;
import Service.ConnectionErrorException;
import MarketEntities.Subscribing.IssueRequests.AIssueRequestSubManager;
import MarketEntities.Subscribing.IssueRequests.IIssueRequestSub;
import MarketEntities.Subscribing.IssueRequests.XvsmIssueRequestSubManager;
import MarketEntities.Subscribing.MarketValues.AStockPricesSubManager;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.MarketValues.XvsmStockPricesSubManager;
import MarketEntities.Subscribing.TradeOrders.ATradeOrderSubManager;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.Subscribing.TradeOrders.XvsmTradeOrderSubManager;
import Util.TransactionTimeout;
import Util.XvsmUtil;
import org.mozartspaces.core.MzsCoreException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmFactory implements IFactory {

    private XvsmUtil.XvsmConnection xc;

    public XvsmFactory(String uri) throws ConnectionErrorException {
        try {
            this.xc = XvsmUtil.initConnection(uri, false);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public IssueRequestContainer newIssueRequestContainer() {
        return new XvsmIssueRequestContainer();
    }

    @Override
    public TradeOrderContainer newTradeOrdersContainer() {
        return new XvsmTradeOrdersContainer();
    }

    @Override
    public StockPricesContainer newStockPricesContainer() {
        return new XvsmStockPricesContainer();
    }

    @Override
    public TransactionHistoryContainer newTransactionHistoryContainer() {
        return new XvsmTransactionHistoryContainer();
    }

    @Override
    public BrokerSupportContainer newBrokerSupportContainer() {
        return new XvsmBrokerSupportContainer();
    }

    @Override
    public FondsIndexContainer newFondsIndexContainer() {
        return new XvsmFondsIndexContainer();
    }

    @Override
    public AIssueRequestSubManager newIssueRequestSubManager(IIssueRequestSub subscription) {
        return new XvsmIssueRequestSubManager(subscription);
    }

    @Override
    public ATradeOrderSubManager newTradeOrderSubManager(ITradeOrderSub subscription) {
        return new XvsmTradeOrderSubManager(subscription);
    }

    @Override
    public AStockPricesSubManager newStockPricesSubManager(IStockPricesSub subscription) {
        return new XvsmStockPricesSubManager(subscription);
    }

    @Override
    public ATransactionHistorySubManager newTransactionHistorySubManager(ITransactionHistorySub subscription) {
        return new XvsmTransactionHistorySubManager(subscription);
    }

    @Override
    public AInvestorDepotSubManager newInvestorDepotSubManager(IInvestorDepotSub subscription) {
        return new XvsmInvestorDepotSubManager(subscription);
    }

    @Override
    public DepotInvestor newDepotInvestor(Investor investor, String transactionId) throws ConnectionErrorException {
        return new XvsmDepotInvestor(investor, transactionId);
    }

    @Override
    public DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionErrorException {
        return new XvsmDepotCompany(comp, transactionId);
    }

    @Override
    public String createTransaction(TransactionTimeout timeout) throws ConnectionErrorException {
        try {
            return XvsmUtil.createTransaction(timeout);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void removeTransaction(String transactionId) {
        XvsmUtil.removeTransaction(transactionId);
    }

    @Override
    public void commitTransaction(String transactionId) throws ConnectionErrorException {
        try {
            XvsmUtil.commitTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }



    @Override
    public void rollbackTransaction(String transactionId) throws ConnectionErrorException {
        try {
            XvsmUtil.rollbackTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void destroy() {

        XvsmUtil.rollbackOpenTransactions();

        xc.getCore().shutdown(false);
        TimerTask shutdownTask = new TimerTask() {

            @Override
            public void run() {
                System.exit(0);
            }
        };
        Timer shutdownTimer = new Timer();
        shutdownTimer.schedule(shutdownTask,250);
    }

    @Override
    public String getProtocolString() {
        return "XVSM";
    }
}
