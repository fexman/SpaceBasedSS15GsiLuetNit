package MarketEntities.RMI;

import MarketEntities.BrokerSupportContainer;
import Model.MarketValue;
import Model.TradeOrder;
import Service.ConnectionError;

import java.util.List;

/**
 * Created by j0h1 on 06.05.2015.
 */
public class RmiBrokerSupportContainer extends BrokerSupportContainer {

    @Override
    public List<TradeOrder> takeNewTradeOrders(String transactionId) throws ConnectionError {
        return null;
    }

    @Override
    public List<MarketValue> takeNewStockPrices(String transactionId) throws ConnectionError {
        return null;
    }

}
