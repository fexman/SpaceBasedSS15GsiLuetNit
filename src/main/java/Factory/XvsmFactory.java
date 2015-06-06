package Factory;

import MarketEntities.*;
import MarketEntities.Subscribing.InvestorDepot.AInvestorDepotSubManager;
import MarketEntities.Subscribing.InvestorDepot.IInvestorDepotSub;
import MarketEntities.Subscribing.InvestorDepot.XvsmInvestorDepotSubManager;
import MarketEntities.Subscribing.TransactionHistory.ATransactionHistorySubManager;
import MarketEntities.Subscribing.TransactionHistory.ITransactionHistorySub;
import MarketEntities.Subscribing.TransactionHistory.XvsmTransactionHistorySubManager;
import MarketEntities.XVSM.*;
import Model.AddressInfo;
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

    private XvsmUtil util;
    private AddressInfo address;

    public XvsmFactory(String uri) throws ConnectionErrorException {
        try {
            this.util = new XvsmUtil(uri,false);
            this.address = new AddressInfo(uri, AddressInfo.Protocol.XVSM);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public IssueRequestContainer newIssueRequestContainer() {
        return new XvsmIssueRequestContainer(util);
    }

    @Override
    public TradeOrderContainer newTradeOrdersContainer() {
        return new XvsmTradeOrdersContainer(util);
    }

    @Override
    public StockPricesContainer newStockPricesContainer() {
        return new XvsmStockPricesContainer(util);
    }

    @Override
    public TransactionHistoryContainer newTransactionHistoryContainer() {
        return new XvsmTransactionHistoryContainer(util);
    }

    @Override
    public BrokerSupportContainer newBrokerSupportContainer() {
        return new XvsmBrokerSupportContainer(util);
    }

    @Override
    public FondsIndexContainer newFondsIndexContainer() {
        return new XvsmFondsIndexContainer(util);
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
        return new XvsmDepotInvestor(util, investor, transactionId);
    }

    @Override
    public DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionErrorException {
        return new XvsmDepotCompany(util, comp, transactionId);
    }

    @Override
    public String createTransaction(TransactionTimeout timeout) throws ConnectionErrorException {
        try {
            return util.createTransaction(timeout);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void removeTransaction(String transactionId) {
        util.removeTransaction(transactionId);
    }

    @Override
    public void commitTransaction(String transactionId) throws ConnectionErrorException {
        try {
            util.commitTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }



    @Override
    public void rollbackTransaction(String transactionId) throws ConnectionErrorException {
        try {
            util.rollbackTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void destroy() {

        util.rollbackOpenTransactions();

        util.getXvsmConnection().getCore().shutdown(false);
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
    public AddressInfo getAddressInfo() {
        return address;
    }
}
