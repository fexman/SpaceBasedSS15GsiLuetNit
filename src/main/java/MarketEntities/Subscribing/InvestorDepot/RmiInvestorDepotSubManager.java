package MarketEntities.Subscribing.InvestorDepot;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Stock;
import Model.TradeObject;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by j0h1 on 29.04.2015.
 */
public class RmiInvestorDepotSubManager extends AInvestorDepotSubManager implements IRmiCallback<TradeObject> {

    public RmiInvestorDepotSubManager(IInvestorDepotSub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void newData(List<TradeObject> newData) throws RemoteException {
        List<TradeObject> newTradeObjects = new ArrayList<>();
        for (Serializable s : newData) {
            try {
                TradeObject tradeObject = (TradeObject) s;
                newTradeObjects.add(tradeObject);
            } catch (ClassCastException e) {
                // new budget was pushed
                Double newBudget = (Double) s;
                subscription.pushNewBudget(newBudget.doubleValue());
                return;
            }
        }
        subscription.pushNewTradeObjects(newTradeObjects);
    }

}
