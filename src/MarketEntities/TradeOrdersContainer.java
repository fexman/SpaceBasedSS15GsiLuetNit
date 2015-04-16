package MarketEntities;

import Model.Company;
import Model.TradeOrder;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class TradeOrdersContainer {

    public abstract void addOrUpdateOrder(TradeOrder order, String transactionId);

    public abstract List<TradeOrder> getOrders(TradeOrder order, String transactionId);

    public abstract List<TradeOrder> getAllorders(String transactionId);
}
