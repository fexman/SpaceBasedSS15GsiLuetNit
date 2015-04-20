package Factory;

import MarketEntities.DepotCompany;
import MarketEntities.IssueStockRequestContainer;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.Company;
import Model.Investor;
import Service.Broker;
import Service.ConnectionError;
import Service.Subscribing.IssueStockRequests.AIssueStockRequestSubManager;
import Service.Subscribing.IssueStockRequests.IIssueStockRequestSub;
import Service.Subscribing.MarketValues.AStockPricesSubManager;
import Service.Subscribing.MarketValues.IStockPricesSub;
import Service.Subscribing.TradeOrders.ATradeOrderSubManager;
import Service.Subscribing.TradeOrders.ITradeOrderSub;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IFactory {

    //Static containers

    IssueStockRequestContainer newISRContainer();

    TradeOrderContainer newTradeOrdersContainer();

    StockPricesContainer newStockPricesContainer();

    //Subscription-manager

    AIssueStockRequestSubManager newIssueStockRequestSubManager(IIssueStockRequestSub subscription);

    ATradeOrderSubManager newTradeOrderSubManager(ITradeOrderSub subscription);

    AStockPricesSubManager newStockPricesSubManager(IStockPricesSub subscription);

    //Dynamic containers

    DepotCompany newDepotInvestor(Investor investor, String transactionId) throws ConnectionError;

    DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionError;

    //Transaction stuff

    String createTransaction() throws ConnectionError;

    void commitTransaction(String transactionId) throws ConnectionError;

    void rollbackTransaction(String transactionId) throws ConnectionError;

    //Closing ressources

    void destroy();

}
