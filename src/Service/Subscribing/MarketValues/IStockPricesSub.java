package Service.Subscribing.MarketValues;

import Model.MarketValue;
import Model.TradeOrder;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IStockPricesSub {

    void pushNewMarketValues(List<MarketValue> newMarketValues);

}
