package MarketEntities;

import Model.MarketValue;
import Model.TradeOrder;
import Service.ConnectionErrorException;
import Service.TransactionTimeoutException;

import java.util.List;

/**
 * Created by Felix on 04.05.2015.
 */
public abstract class BrokerSupportContainer {

        public abstract List<TradeOrder> takeNewTradeOrders(String transactionId) throws ConnectionErrorException, TransactionTimeoutException;

        public abstract List<MarketValue> takeNewStockPrices(String transactionId) throws ConnectionErrorException, TransactionTimeoutException;

}
