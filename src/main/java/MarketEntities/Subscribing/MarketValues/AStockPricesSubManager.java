package MarketEntities.Subscribing.MarketValues;

import MarketEntities.Subscribing.ASubManager;

/**
 * Created by Felix on 16.04.2015.
 */
public class AStockPricesSubManager extends ASubManager<IStockPricesSub> {

    public AStockPricesSubManager(IStockPricesSub subscription) {
        super(subscription);
    }
}
