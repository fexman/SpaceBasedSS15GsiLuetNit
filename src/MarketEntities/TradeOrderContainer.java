package MarketEntities;

import Model.TradeOrder;
import Service.ConnectionError;
import Service.Subscribing.TradeOrders.ATradeOrderSubManager;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class TradeOrderContainer {

    public abstract void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionError;

    public abstract List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionError;

    public abstract List<TradeOrder> getAllOrders(String transactionId) throws ConnectionError;

    public abstract void subscribe(ATradeOrderSubManager subscriber, String transactionId) throws ConnectionError;
}
