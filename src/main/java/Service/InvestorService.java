package Service;

import Factory.IFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.IssueRequestContainer;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import MarketEntities.TradeOrderContainer;
import Model.Investor;
import Model.IssueFondsRequest;
import Model.TradeOrder;
import Util.TransactionTimeout;

/**
 * Created by j0h1 on 24.04.2015.
 */
public class InvestorService extends Service implements ITradeOrderSub {

    private Investor investor;
    private DepotInvestor depotInvestor;
    private TradeOrderContainer tradeOrderContainer;
    private IssueRequestContainer irContainer;

    public InvestorService(IFactory factory) {
        super(factory);
    }

    public InvestorService(IFactory factory, Investor investor) {
        super(factory);
        this.investor = investor;
        this.tradeOrderContainer = factory.newTradeOrdersContainer();
        this.irContainer = factory.newIssueRequestContainer();
        try {
            this.depotInvestor = factory.newDepotInvestor(investor,null);
        } catch (ConnectionErrorException e)  {
            throw new RuntimeException("COULD NOT CREATE DEPOT FOR INVESTOR SERVICE");
        }

    }

    @Override
    public void pushNewTradeOrders(TradeOrder tradeOrder) {
        String transactionId = "";

        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

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

            depotInvestor.addToBudget(amountToBeAdded, transactionId);

            factory.commitTransaction(transactionId);

            System.out.println("Added " + amountToBeAdded + " to " + investor.getId() + "'s  budget.");
        } catch (ConnectionErrorException e) {
            factory.rollbackTransaction(transactionId);
            throw e;
        }
    }

    public void issueFonds(int amount) throws ConnectionErrorException {
        if (investor.isFonds()) {
            String transactionId = "";

            try {
                transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

                IssueFondsRequest ifr = new IssueFondsRequest(investor,amount);

                //Write to investor-depot
                System.out.print("Writing new fonds to depot ... ");
                depotInvestor.addTradeObjects(ifr.toTradeObjects(), transactionId);
                System.out.println("done.");

                //Issue Stocks
                System.out.print("Writing IF-request to container ... ");
                irContainer.addIssueRequest(ifr, transactionId);
                System.out.println("done.");

                factory.commitTransaction(transactionId);
            } catch (ConnectionErrorException e) {
                factory.rollbackTransaction(transactionId);
                throw e;
            }
        } else {
            System.out.println("Investor is no fondmanager, cannot issue Fonds.");
        }
    }

}
