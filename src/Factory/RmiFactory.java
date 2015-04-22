package Factory;


import MarketEntities.DepotCompany;
import MarketEntities.IssueStockRequestContainer;
import MarketEntities.RMI.RmiDepotCompany;
import MarketEntities.RMI.RmiIssueStockRequestContainer;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.Company;
import Model.Investor;
import RMIServer.IRmiServer;
import RMIServer.RmiServer;
import Service.ConnectionError;
import Service.Subscribing.IssueStockRequests.AIssueStockRequestSubManager;
import Service.Subscribing.IssueStockRequests.IIssueStockRequestSub;
import Service.Subscribing.MarketValues.AStockPricesSubManager;
import Service.Subscribing.MarketValues.IStockPricesSub;
import Service.Subscribing.TradeOrders.ATradeOrderSubManager;
import Service.Subscribing.TradeOrders.ITradeOrderSub;
import Util.RmiUtil;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiFactory implements IFactory{

    RmiUtil.RmiConnection rc;

    public RmiFactory(String uri) throws ConnectionError {
        rc = RmiUtil.initConnection(uri);
    }

    @Override
    public IssueStockRequestContainer newISRContainer() {
        return new RmiIssueStockRequestContainer();
    }

    @Override
    public TradeOrderContainer newTradeOrdersContainer() {
        return null;
    }

    @Override
    public StockPricesContainer newStockPricesContainer() {
        return null;
    }

    @Override
    public AIssueStockRequestSubManager newIssueStockRequestSubManager(IIssueStockRequestSub subscription) {
        return null;
    }

    @Override
    public ATradeOrderSubManager newTradeOrderSubManager(ITradeOrderSub subscription) {
        return null;
    }

    @Override
    public AStockPricesSubManager newStockPricesSubManager(IStockPricesSub subscription) {
        return null;
    }

    @Override
    public DepotCompany newDepotInvestor(Investor investor, String transactionId) throws ConnectionError {
        return null;
    }

    @Override
    public DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionError {
        return new RmiDepotCompany(comp,transactionId);
    }

    @Override
    public String createTransaction() throws ConnectionError {
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
