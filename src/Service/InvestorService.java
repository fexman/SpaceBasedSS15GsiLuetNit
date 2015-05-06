package Service;

import Factory.IFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.TradeOrderContainer;
import Model.Investor;
import Model.TradeOrder;
import Util.TransactionTimeout;

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
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            // get trade order container instance
            TradeOrderContainer tradeOrderContainer = factory.newTradeOrdersContainer();

            tradeOrderContainer.addOrUpdateOrder(tradeOrder, transactionId);

            factory.commitTransaction(transactionId);
            System.out.println("Committed: " + tradeOrder);
        } catch (ConnectionErrorException e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw e;
            } catch (ConnectionErrorException ex) {
                System.out.println("Error on tradeOrders push");
                ex.printStackTrace();
            }
        }
    }

    public void addToBudget(double amountToBeAdded) throws ConnectionErrorException {
        String transactionId = "";

        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            DepotInvestor depotInvestor = factory.newDepotInvestor(investor, transactionId);

            depotInvestor.addToBudget(amountToBeAdded, transactionId);

            factory.commitTransaction(transactionId);

            System.out.println("Added " + amountToBeAdded + " to " + investor.getId() + "'s  budget.");
        } catch (ConnectionErrorException e) {
            factory.rollbackTransaction(transactionId);
            throw e;
        }
    }

}
