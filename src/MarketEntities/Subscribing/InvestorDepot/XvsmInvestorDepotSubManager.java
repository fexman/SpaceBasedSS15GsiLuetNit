package MarketEntities.Subscribing.InvestorDepot;

import MarketEntities.Subscribing.IssueStockRequests.AISRSubManager;
import Model.IssueStockRequest;
import Model.Stock;
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
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> stocks) {
        List<Stock> newStocks = new ArrayList<>();
        for (Serializable s : stocks) {
            try {
                Stock stock = (Stock) ((Entry) s).getValue();
                newStocks.add(stock);
            } catch (ClassCastException e) {
                // new budget was pushed
                Double newBudget = (Double) ((Entry) s).getValue();
                subscription.pushNewBudget(newBudget.doubleValue());
                return;
            }
        }
        subscription.pushNewStocks(newStocks);
    }

}
