package Service.Subscribing.TradeOrders;

import Model.TradeOrder;
import org.mozartspaces.core.Entry;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmTradeOrderSubManager extends ATradeOrderSubManager implements NotificationListener {

    public XvsmTradeOrderSubManager(ITradeOrderSub subscription) {
        super(subscription);
    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        List<TradeOrder> newTradeOrders = new ArrayList<>();
        for (Serializable e: entries) {
            TradeOrder to = (TradeOrder)((Entry) e).getValue();
            newTradeOrders.add(to);
        }
        subscription.pushNewTradeOrders(newTradeOrders);
    }
}
