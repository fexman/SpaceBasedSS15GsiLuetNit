package Service;

import Factory.IFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.StockPricesContainer;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import Util.XvsmUtil;

/**
 * Created by j0h1 on 24.04.2015.
 */
public class InvestorService extends Service implements ITradeOrderSub {

    public InvestorService(IFactory factory) {
        super(factory);
    }

    @Override
    public void pushNewTradeOrders(TradeOrder tradeOrder) {
        String transactionId = "";

        try {
            transactionId = factory.createTransaction();

            // get trade order container instance
            TradeOrderContainer tradeOrderContainer = factory.newTradeOrdersContainer();

            tradeOrderContainer.addOrUpdateOrder(tradeOrder, transactionId);
            factory.commitTransaction(transactionId);
            System.out.println("Committed: " + tradeOrder);
        } catch (ConnectionError e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw e;
            } catch (ConnectionError ex) {
                System.out.println("Error on tradeOrders push");
                ex.printStackTrace();
            }
        }
    }

}
