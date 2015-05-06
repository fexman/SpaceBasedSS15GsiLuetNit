package Factory;

import MarketEntities.*;
import MarketEntities.Subscribing.InvestorDepot.AInvestorDepotSubManager;
import MarketEntities.Subscribing.InvestorDepot.IInvestorDepotSub;
import MarketEntities.Subscribing.TransactionHistory.ATransactionHistorySubManager;
import MarketEntities.Subscribing.TransactionHistory.ITransactionHistorySub;
import Model.Company;
import Model.Investor;
import Service.ConnectionErrorException;
import MarketEntities.Subscribing.IssueStockRequests.AISRSubManager;
import MarketEntities.Subscribing.IssueStockRequests.IISRRequestSub;
import MarketEntities.Subscribing.MarketValues.AStockPricesSubManager;
import MarketEntities.Subscribing.MarketValues.IStockPricesSub;
import MarketEntities.Subscribing.TradeOrders.ATradeOrderSubManager;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import Util.TransactionTimeout;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IFactory {

    //Static containers

    ISRContainer newISRContainer();

    TradeOrderContainer newTradeOrdersContainer();

    StockPricesContainer newStockPricesContainer();

    TransactionHistoryContainer newTransactionHistoryContainer();

    BrokerSupportContainer newBrokerSupportContainer();

    //Subscription-manager

    AISRSubManager newIssueStockRequestSubManager(IISRRequestSub subscription);

    ATradeOrderSubManager newTradeOrderSubManager(ITradeOrderSub subscription);

    AStockPricesSubManager newStockPricesSubManager(IStockPricesSub subscription);

    ATransactionHistorySubManager newTransactionHistorySubManager(ITransactionHistorySub subscription);

    AInvestorDepotSubManager newInvestorDepotSubManager(IInvestorDepotSub subscription);

    //Dynamic containers

    DepotInvestor newDepotInvestor(Investor investor, String transactionId) throws ConnectionErrorException;

    DepotCompany newDepotCompany(Company comp, String transactionId) throws ConnectionErrorException;

    //Transaction stuff

    String createTransaction(TransactionTimeout timeout) throws ConnectionErrorException;

    void removeTransaction(String transactionId);

    void commitTransaction(String transactionId) throws ConnectionErrorException;

    void rollbackTransaction(String transactionId) throws ConnectionErrorException;

    //Closing ressources

    void destroy();

}
