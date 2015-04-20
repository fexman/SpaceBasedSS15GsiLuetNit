package Service.Subscribing.MarketValues;

/**
 * Created by Felix on 16.04.2015.
 */
public class AStockPricesSubManager {

    protected IStockPricesSub subscription;

    public AStockPricesSubManager(IStockPricesSub subscription) {
        this.subscription = subscription;
    }
}
