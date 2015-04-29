package MarketEntities.Subscribing.InvestorDepot;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Stock;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by j0h1 on 29.04.2015.
 */
public class RmiInvestorDepotSubManager extends AInvestorDepotSubManager implements IRmiCallback<Stock> {

    public RmiInvestorDepotSubManager(IInvestorDepotSub subscription) {
        super(subscription);
    }

    @Override
    public void newData(List<Stock> newData) throws RemoteException {
        subscription.pushNewStocks(newData);
    }

}
