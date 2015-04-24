package Factory;

import MarketEntities.DepotCompany;
import MarketEntities.IssueStockRequestContainer;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import MarketEntities.XVSM.XvsmDepotCompany;
import MarketEntities.XVSM.XvsmIssueStockRequestContainer;
import MarketEntities.XVSM.XvsmStockPricesContainer;
import MarketEntities.XVSM.XvsmTradeOrdersContainer;
import Model.Company;
import Model.Investor;
import Service.ConnectionError;
import MarketEntities.Subscribing.IssueStockRequests.AISRSubManager;
import MarketEntities.Subscribing.IssueStockRequests.IISRRequestSub;
import MarketEntities.Subscribing.IssueStockRequests.XvsmISRSubManager;
import MarketEntities.Subscribing.MarketValues.AStockPricesSubManager;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.MarketValues.XvsmStockPricesSubManager;
import MarketEntities.Subscribing.TradeOrders.ATradeOrderSubManager;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.Subscribing.TradeOrders.XvsmTradeOrderSubManager;
import Util.XvsmUtil;
import org.mozartspaces.core.MzsCoreException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmFactory implements IFactory {

    private XvsmUtil.XvsmConnection xc;

    public XvsmFactory(String uri) throws ConnectionError {
        try {
            this.xc = XvsmUtil.initConnection(uri);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public IssueStockRequestContainer newISRContainer() {
        return new XvsmIssueStockRequestContainer();
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
    public AISRSubManager newIssueStockRequestSubManager(IISRRequestSub subscription) {
        return new XvsmISRSubManager(subscription);
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
    public DepotCompany newDepotInvestor(Investor investor, String transactionId) throws ConnectionError {
        return null;
    }

    @Override
    public DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionError {
        return new XvsmDepotCompany(comp, transactionId);
    }

    @Override
    public String createTransaction() throws ConnectionError {
        try {
            return XvsmUtil.createTransaction();
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void commitTransaction(String transactionId) throws ConnectionError {
        try {
            XvsmUtil.commitTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void rollbackTransaction(String transactionId) throws ConnectionError {
        try {
            XvsmUtil.rollbackTransaction(transactionId);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void destroy() {
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
}
