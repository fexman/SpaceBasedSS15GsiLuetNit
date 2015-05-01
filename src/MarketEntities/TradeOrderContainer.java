package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.TradeOrder;
import Service.ConnectionError;
import MarketEntities.Subscribing.TradeOrders.ATradeOrderSubManager;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class TradeOrderContainer implements ISubscribeable {

    public abstract void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionError;

    public abstract List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionError;

    public abstract TradeOrder takeOrder(TradeOrder tradeOrder, String transactionId) throws ConnectionError;

    public abstract List<TradeOrder> getAllOrders(String transactionId) throws ConnectionError;

}
