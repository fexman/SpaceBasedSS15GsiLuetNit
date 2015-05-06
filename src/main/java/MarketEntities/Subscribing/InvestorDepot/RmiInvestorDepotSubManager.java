package MarketEntities.Subscribing.InvestorDepot;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Stock;
import org.mozartspaces.core.Entry;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by j0h1 on 29.04.2015.
 */
public class RmiInvestorDepotSubManager extends AInvestorDepotSubManager implements IRmiCallback<Stock> {

    public RmiInvestorDepotSubManager(IInvestorDepotSub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void newData(List<Stock> newData) throws RemoteException {
        List<Stock> newStocks = new ArrayList<>();
        for (Serializable s : newData) {
            try {
                Stock stock = (Stock) s;
                newStocks.add(stock);
            } catch (ClassCastException e) {
                // new budget was pushed
                Double newBudget = (Double) s;
                subscription.pushNewBudget(newBudget.doubleValue());
                return;
            }
        }
        subscription.pushNewStocks(newStocks);
    }

}
