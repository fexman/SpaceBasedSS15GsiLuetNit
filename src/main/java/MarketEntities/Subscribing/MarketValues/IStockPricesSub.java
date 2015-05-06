package MarketEntities.Subscribing.MarketValues;

import MarketEntities.Subscribing.Subscription;
import Model.MarketValue;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IStockPricesSub extends Subscription {

    void pushNewMarketValues(List<MarketValue> newMarketValues);
}
