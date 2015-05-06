package MarketEntities.Subscribing.MarketValues;

import Model.MarketValue;
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
public class XvsmStockPricesSubManager extends AStockPricesSubManager implements NotificationListener {


    public XvsmStockPricesSubManager(IStockPricesSub subscription) {
        super(subscription);
    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        List<MarketValue> newMarketValues = new ArrayList<>();
        for (Serializable e: entries) {
            MarketValue mw = (MarketValue)((Entry) e).getValue();
            newMarketValues.add(mw);
        }
        subscription.pushNewMarketValues(newMarketValues);
    }
}
