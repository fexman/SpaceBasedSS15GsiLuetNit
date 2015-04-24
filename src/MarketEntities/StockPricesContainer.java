package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.Company;
import Model.MarketValue;
import Service.ConnectionError;
import MarketEntities.Subscribing.MarketValues.AStockPricesSubManager;

import java.util.List;

/**
 * Created by Felix on 20.04.2015.
 */
public abstract class StockPricesContainer implements ISubscribeable {

    public abstract void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws ConnectionError;

    public abstract MarketValue getMarketValue(Company comp, String transactionId) throws ConnectionError;

    public abstract List<MarketValue> getAll(String transactionId) throws ConnectionError;

}
