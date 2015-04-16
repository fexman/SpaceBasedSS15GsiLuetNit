package Service.Subscribing.TradeOrders;

/**
 * Created by Felix on 16.04.2015.
 */
public class ATradeOrderSubManager {

    protected ITradeOrderSub subscription;

    public ATradeOrderSubManager(ITradeOrderSub subscription) {
        this.subscription = subscription;
    }
}
