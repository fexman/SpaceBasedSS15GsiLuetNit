package MarketEntities.Subscribing.InvestorDepot;

import Model.TradeObject;
import org.mozartspaces.core.Entry;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by j0h1 on 29.04.2015.
 */
public class XvsmInvestorDepotSubManager extends AInvestorDepotSubManager implements NotificationListener {

    public XvsmInvestorDepotSubManager(IInvestorDepotSub subscription) {
        super(subscription);
    }

    @Override
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> tradeObjects) {
        List<TradeObject> newTradeObjects = new ArrayList<>();
        for (Serializable s : tradeObjects) {
            try {
                TradeObject tradeObject = (TradeObject) ((Entry) s).getValue();
                newTradeObjects.add(tradeObject);
            } catch (ClassCastException e) {
                // new budget was pushed
                Double newBudget = (Double) ((Entry) s).getValue();
                subscription.pushNewBudget(newBudget.doubleValue());
                return;
            }
        }
        subscription.pushNewTradeObjects(newTradeObjects);
    }

}
