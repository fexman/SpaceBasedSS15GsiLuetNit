package Service;

import Factory.IFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.TradeOrderContainer;
import Model.Investor;
import Model.TradeOrder;

/**
 * Created by j0h1 on 24.04.2015.
 */
public class InvestorService extends Service implements ITradeOrderSub {

    private Investor investor;

    public InvestorService(IFactory factory) {
        super(factory);
    }

    public InvestorService(IFactory factory, Investor investor) {
        super(factory);
        this.investor = investor;
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

    public void addToBudget(double amountToBeAdded) throws ConnectionError{
        String transactionId = "";

        try {
            transactionId = factory.createTransaction();

            DepotInvestor depotInvestor = factory.newDepotInvestor(investor, transactionId);

            depotInvestor.addToBudget(amountToBeAdded, transactionId);

            factory.commitTransaction(transactionId);

            System.out.println("Added " + amountToBeAdded + " to " + investor.getId() + "'s  budget.");
        } catch (ConnectionError e) {
            factory.rollbackTransaction(transactionId);
            throw e;
        }
    }

}
