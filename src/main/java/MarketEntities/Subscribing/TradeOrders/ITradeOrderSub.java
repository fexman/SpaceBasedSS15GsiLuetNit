package MarketEntities.Subscribing.TradeOrders;

import MarketEntities.Subscribing.Subscription;
import Model.TradeOrder;

/**
 * Created by Felix on 16.04.2015.
 */
public interface ITradeOrderSub extends Subscription {

    void pushNewTradeOrders(TradeOrder tradeOrder);

}
