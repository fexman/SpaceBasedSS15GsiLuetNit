package Service;

import Factory.IFactory;
import MarketEntities.*;
import Model.*;
import MarketEntities.Subscribing.IssueStockRequests.IISRRequestSub;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import Util.XvsmUtil;

import java.util.List;

/**
 * Created by Felix on 11.04.2015.
 */
public class BrokerService extends Service implements IISRRequestSub, ITradeOrderSub {

    private IssueStockRequestContainer isrContainer;
    private TradeOrderContainer tradeOrdersContainer;
    private StockPricesContainer stockPricesContainer;

    public BrokerService(IFactory factory) {
        super(factory);
        isrContainer = factory.newISRContainer();
        tradeOrdersContainer = factory.newTradeOrdersContainer();
        stockPricesContainer = factory.newStockPricesContainer();
    }


    public void startBroking() throws ConnectionError {
            takeAndProcessISRs();
            isrContainer.subscribe(factory.newIssueStockRequestSubManager(this), null);
            tradeOrdersContainer.subscribe(factory.newTradeOrderSubManager(this),null);
    }

    public void takeAndProcessISRs() throws ConnectionError {
        String transactionId = null;
        try {

            transactionId = factory.createTransaction();
            List<IssueStockRequest> isrs = isrContainer.takeIssueStockRequests(transactionId);
            
            if (isrs.size() > 0) {
                System.out.println("Got " + isrs.size() + " new ISRs!");
                for (IssueStockRequest isr : isrs) { //PROCESS NEW ISRS

                    //Set market Value if new stocks
                    MarketValue mw = stockPricesContainer.getMarketValue(isr.getCompany(),transactionId);
                    if (mw == null){
                        System.out.println("Setting new marketValue for "+isr.getCompany()+" on ISR-price.");
                        mw = new MarketValue(isr.getCompany(),isr.getPrice());
                        stockPricesContainer.addOrUpdateMarketValue(mw,transactionId);
                    }

                    //Create Trade Order
                    TradeOrder order = new TradeOrder(isr.getCompany(),isr.getCompany(),isr.getAmount(),isr.getPrice());
                    tradeOrdersContainer.addOrUpdateOrder(order, transactionId);
                }
            }

            factory.commitTransaction(transactionId);

        } catch (ConnectionError e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw new ConnectionError(e);
            } catch (ConnectionError ex) {
                throw new ConnectionError(ex);
            }
        }

    }

    @Override
    public void pushNewTradeOrders(TradeOrder tradeOrder) throws ConnectionError {
        System.out.println("Trade Orders Callback.");

        String transactionId = "";
        try {
            transactionId = factory.createTransaction();

            // get investor container
            Depot investorDepot;
            if (tradeOrder.getInvestorType() == TradeOrder.InvestorType.COMPANY) {
                investorDepot = factory.newDepotCompany(new Company(tradeOrder.getId()), transactionId);
            } else {
                investorDepot = factory.newDepotInvestor(new Investor(tradeOrder.getId()), transactionId);
            }

            //TODO: CORRECT THIS
            // validate if transaction is possible
            /*if (validateTransaction(factory, investorDepot, tradeOrder, transactionId)) {
                tradeOrdersContainer.addOrUpdateOrder(tradeOrder, transactionId);
                factory.commitTransaction(transactionId);
                System.out.println("Committed: " + tradeOrder);
            } else {
                // TODO punish investor for not having his shit together
            }*/
        } catch (ConnectionError e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw e;
            } catch (ConnectionError ex) {
                throw ex;
            }
        }
    }

    @Override
    public void pushNewISRs(List<IssueStockRequest> newISRs) {
        try {
            takeAndProcessISRs();
        } catch (ConnectionError connectionError) {
            System.out.println("FATAL ERROR: Connection Error on subscription-PUSH.");
        }
    }

    private boolean validateTransaction(IFactory factory, DepotInvestor depotInvestor, TradeOrder tradeOrder, String transactionId) throws ConnectionError {
        if (tradeOrder.getType().equals(TradeOrder.Type.BUY_ORDER)) {
            // check if dude has enough cash money to perform transaction
            StockPricesContainer stockPricesContainer = factory.newStockPricesContainer();
            double currentMarketValue = stockPricesContainer.getMarketValue(tradeOrder.getCompany(), transactionId).getPrice();
            double transactionCost = (double) tradeOrder.getPendingAmount() * currentMarketValue;

            return depotInvestor.getBudget(transactionId) >= transactionCost;
        } else if (tradeOrder.getType().equals(TradeOrder.Type.SELL_ORDER)) {
            // check if dude has enough stocks in his pocket
            return depotInvestor.getStockAmount(tradeOrder.getCompanyId(), transactionId) >= tradeOrder.getPendingAmount();
        }
        return false;
    }
}
