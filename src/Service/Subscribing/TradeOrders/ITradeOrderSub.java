package Service.Subscribing.TradeOrders;

import Model.TradeOrder;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public interface ITradeOrderSub {

    void pushNewTradeOrders(List<TradeOrder> newTradeOrders);

}
