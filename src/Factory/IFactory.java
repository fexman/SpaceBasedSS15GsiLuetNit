package Factory;

import MarketEntities.*;
import MarketEntities.Subscribing.TransactionHistory.ATransactionHistorySubManager;
import MarketEntities.Subscribing.TransactionHistory.ITransactionHistorySub;
import Model.Company;
import Model.Investor;
import Service.ConnectionError;
import MarketEntities.Subscribing.IssueStockRequests.AISRSubManager;
import MarketEntities.Subscribing.IssueStockRequests.IISRRequestSub;
import MarketEntities.Subscribing.MarketValues.AStockPricesSubManager;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.TradeOrders.ATradeOrderSubManager;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IFactory {

    //Static containers

    ISRContainer newISRContainer();

    TradeOrderContainer newTradeOrdersContainer();

    StockPricesContainer newStockPricesContainer();

    TransactionHistoryContainer newTransactionHistoryContainer();

    //Subscription-manager

    AISRSubManager newIssueStockRequestSubManager(IISRRequestSub subscription);

    ATradeOrderSubManager newTradeOrderSubManager(ITradeOrderSub subscription);

    AStockPricesSubManager newStockPricesSubManager(IStockPricesSub subscription);

    ATransactionHistorySubManager newTransactionHistorySubManager(ITransactionHistorySub subscription);

    //Dynamic containers

    DepotInvestor newDepotInvestor(Investor investor, String transactionId) throws ConnectionError;

    DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionError;

    //Transaction stuff

    String createTransaction() throws ConnectionError;

    void commitTransaction(String transactionId) throws ConnectionError;

    void rollbackTransaction(String transactionId) throws ConnectionError;

    //Closing ressources

    void destroy();

}
