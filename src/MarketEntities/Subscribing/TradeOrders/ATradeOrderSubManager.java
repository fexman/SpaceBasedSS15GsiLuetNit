package MarketEntities.Subscribing.TradeOrders;

import MarketEntities.Subscribing.ASubManager;

/**
 * Created by Felix on 16.04.2015.
 */
public class ATradeOrderSubManager extends ASubManager<ITradeOrderSub> {

    public ATradeOrderSubManager(ITradeOrderSub subscription) {
        super(subscription);
    }
}
