package MarketEntities;

import Model.MarketValue;
import Model.TradeOrder;
import Service.ConnectionError;

import java.util.List;

/**
 * Created by Felix on 04.05.2015.
 */
public abstract class BrokerSupportContainer {

        public abstract List<TradeOrder> takeNewTradeOrders(String transactionId) throws ConnectionError;

        public abstract List<MarketValue> takeNewStockPrices(String transactionId) throws ConnectionError;

}
