package Factory;

import MarketEntities.DepotCompany;
import MarketEntities.ISRContainer;
import MarketEntities.TradeOrdersContainer;
import Model.Company;
import Model.Investor;
import Service.Broker;
import Service.ConnectionError;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IFactory {

    //Static containers + more

    ISRContainer newISRContainer();

    TradeOrdersContainer newTradeOrdersContainer();

    AbstractSubscriber newSubscriber(Broker broker);

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
