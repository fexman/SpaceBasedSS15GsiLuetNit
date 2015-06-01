package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.Company;
import Model.MarketValue;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 20.04.2015.
 */
public abstract class StockPricesContainer implements ISubscribeable {

    public abstract void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws ConnectionErrorException;

    public abstract MarketValue getMarketValue(String id, String transactionId) throws ConnectionErrorException;

    public abstract List<MarketValue> getAll(String transactionId) throws ConnectionErrorException;

}
