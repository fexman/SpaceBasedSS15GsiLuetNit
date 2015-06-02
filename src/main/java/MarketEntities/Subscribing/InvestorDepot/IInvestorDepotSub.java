package MarketEntities.Subscribing.InvestorDepot;

import MarketEntities.Subscribing.Subscription;
import Model.TradeObject;

import java.util.List;

/**
 * Created by j0h1 on 29.04.2015.
 */
public interface IInvestorDepotSub extends Subscription {

    void pushNewTradeObjects(List<TradeObject> tradeObjects);

    void pushNewBudget(Double budget);

}
