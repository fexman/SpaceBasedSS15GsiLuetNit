package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.TradeOrder;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class TradeOrderContainer implements ISubscribeable {

    public abstract void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionErrorException;

    public abstract List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionErrorException;

    public abstract TradeOrder takeOrder(TradeOrder tradeOrder, String transactionId) throws ConnectionErrorException;

    public abstract List<TradeOrder> getAllOrders(String transactionId) throws ConnectionErrorException;

}
