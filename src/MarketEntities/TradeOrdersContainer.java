package MarketEntities;

import Model.Company;
import Model.TradeOrder;
import Service.ConnectionError;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class TradeOrdersContainer {

    public abstract void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionError;

    public abstract List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionError;

    public abstract List<TradeOrder> getAllorders(String transactionId) throws ConnectionError;
}
