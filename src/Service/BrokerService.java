package Service;

import Factory.IFactory;
import MarketEntities.*;
import MarketEntities.Subscribing.TradeOrders.ATradeOrderSubManager;
import Model.*;
import MarketEntities.Subscribing.IssueStockRequests.IISRRequestSub;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import org.mozartspaces.notifications.NotificationManager;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 11.04.2015.
 */
public class BrokerService extends Service implements IISRRequestSub, ITradeOrderSub {

    private String id;
    private ISRContainer isrContainer;
    private TradeOrderContainer tradeOrdersContainer;
    private StockPricesContainer stockPricesContainer;
    private TransactionHistoryContainer transactionHistoryContainer;

    private final double PROVISION_PERCENTAGE = 0.03;

    public BrokerService(String id, IFactory factory) {
        super(factory);
        this.id = id;
        isrContainer = factory.newISRContainer();
        tradeOrdersContainer = factory.newTradeOrdersContainer();
        stockPricesContainer = factory.newStockPricesContainer();
        transactionHistoryContainer = factory.newTransactionHistoryContainer();
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

                    System.out.println("New Price: "+mw.getPrice());

                    //Create Trade Order
                    TradeOrder order = new TradeOrder(isr.getCompany(),isr.getCompany(),isr.getAmount(),mw.getPrice());
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
    public void pushNewTradeOrders(TradeOrder tradeOrder) {
        System.out.println("Trade Orders Callback.");

        if (tradeOrder.getStatus().equals(TradeOrder.Status.OPEN) || tradeOrder.getStatus().equals(TradeOrder.Status.PARTIALLY_COMPLETED)) {
            solveOrder(tradeOrder);
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


    private void solveOrder(TradeOrder tradeOrder) {
        try {
            String transactionId = factory.createTransaction();

            TradeOrder matchingTradeOrder = findMatchingTradeOrder(tradeOrder, transactionId);

            if (matchingTradeOrder != null) {
                if (tradeOrder.getType().equals(TradeOrder.Type.BUY_ORDER)) {
                    if (validateTradeOrders(tradeOrder, matchingTradeOrder, transactionId)) {
                        executeTransaction(tradeOrder, matchingTradeOrder, transactionId);
                    }
                } else {
                    if (validateTradeOrders(matchingTradeOrder, tradeOrder, transactionId)) {
                        executeTransaction(matchingTradeOrder, tradeOrder, transactionId);
                    }
                }
            }
            // finally commit
            factory.commitTransaction(transactionId);
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }
    }


    private void executeTransaction(TradeOrder buyOrder, TradeOrder sellOrder, String transactionId) throws ConnectionError {
        // buyer can only be an investor
        DepotInvestor depotBuyer = factory.newDepotInvestor(new Investor(buyOrder.getInvestorId()), transactionId);

        Depot depotSeller;
        if (sellOrder.getInvestorType().equals(TradeOrder.InvestorType.COMPANY)) {
            depotSeller = factory.newDepotCompany(sellOrder.getCompany(), transactionId);
        } else {
            depotSeller = factory.newDepotInvestor(new Investor(sellOrder.getInvestorId()), transactionId);
        }

        // list of stocks to transfer from seller to buyer depot
        List<Stock> boughtStocks;

        if (buyOrder.getPendingAmount() >= sellOrder.getPendingAmount()) {
            System.out.println("Taking all stocks (" + sellOrder.getPendingAmount() + ") from seller depot.");
            // take ALL stocks from seller depot
            boughtStocks = takeStocksFromSellerDepot(depotSeller, sellOrder, sellOrder.getPendingAmount(), transactionId);

            if (buyOrder.getPendingAmount() == sellOrder.getPendingAmount()) {
                System.out.println("Buy order completed.");
                buyOrder.setStatus(TradeOrder.Status.COMPLETED); // should be equal to totalAmount when completed
            } else {
                System.out.println("Buy order partially completed.");
                buyOrder.setStatus(TradeOrder.Status.PARTIALLY_COMPLETED);
            }
            buyOrder.setCompletedAmount(buyOrder.getCompletedAmount() + boughtStocks.size());

            System.out.println("Sell order completed.");
            sellOrder.setStatus(TradeOrder.Status.COMPLETED);
            sellOrder.setCompletedAmount(sellOrder.getCompletedAmount() + boughtStocks.size());
        }  else {
            // take the amount required in buy order from seller depot
            boughtStocks = takeStocksFromSellerDepot(depotSeller, sellOrder, buyOrder.getPendingAmount(), transactionId);

            System.out.println("Taking " + boughtStocks.size() + " stocks from seller depot.");


            buyOrder.setStatus(TradeOrder.Status.COMPLETED);
            buyOrder.setCompletedAmount(buyOrder.getCompletedAmount() + boughtStocks.size());

            System.out.println("Sell order partially completed.");
            sellOrder.setStatus(TradeOrder.Status.PARTIALLY_COMPLETED);
            sellOrder.setCompletedAmount(sellOrder.getCompletedAmount() + boughtStocks.size());
        }

        // add stocks to buyer depot
        depotBuyer.addStocks(boughtStocks, transactionId);

        // decrease budget of buyer
        double currentMarketValue = stockPricesContainer.getMarketValue(buyOrder.getCompany(), transactionId).getPrice();
        double totalValue = boughtStocks.size() * currentMarketValue;
        double provision = totalValue * PROVISION_PERCENTAGE;

        System.out.println("Removing " + (totalValue + provision) + " from " + depotBuyer.getDepotName());
        depotBuyer.addToBudget(-(totalValue + provision), transactionId);

        if (sellOrder.getInvestorType().equals(TradeOrder.InvestorType.INVESTOR)) {
            System.out.println("Increasing sellers budget by " + totalValue);
            ((DepotInvestor) depotSeller).addToBudget(totalValue, transactionId);
        }

        // write transaction to transaction history container
        System.out.println("Writing transaction " + transactionId + " to transaction history.");
        transactionHistoryContainer.addHistoryEntry(new HistoryEntry(transactionId, id, new Investor(buyOrder.getInvestorId()), sellOrder.getCompany(), buyOrder.getCompanyId(),
                buyOrder.getId(), sellOrder.getId(), currentMarketValue, boughtStocks.size(), totalValue + provision, provision), transactionId);

        // update trade orders (update completed trade order first to avoid inconsistencies)
        if (buyOrder.getStatus().equals(TradeOrder.Status.COMPLETED)) {
            System.out.println("Updating completed buy order.");
            tradeOrdersContainer.addOrUpdateOrder(buyOrder, transactionId);
            System.out.println("Updating sell order.");
            tradeOrdersContainer.addOrUpdateOrder(sellOrder, transactionId);
        } else {
            System.out.println("Updating completed sell order.");
            tradeOrdersContainer.addOrUpdateOrder(sellOrder, transactionId);
            System.out.println("Updating buy order.");
            tradeOrdersContainer.addOrUpdateOrder(buyOrder, transactionId);
        }
    }


    private List<Stock> takeStocksFromSellerDepot(Depot depotSeller, TradeOrder sellOrder, int amount, String transactionId) throws ConnectionError {
        if (sellOrder.getInvestorType().equals(TradeOrder.InvestorType.COMPANY)) {
            return ((DepotCompany) depotSeller).takeStocks(amount, transactionId);
        } else{
            return ((DepotInvestor) depotSeller).takeStocks(sellOrder.getCompany(), amount, transactionId);
        }
    }


    private boolean validateTradeOrders(TradeOrder buyOrder, TradeOrder sellOrder, String transactionId) throws ConnectionError {
        DepotInvestor depotInvestor = factory.newDepotInvestor(new Investor(buyOrder.getInvestorId()), transactionId);
        if (buyerHasEnoughMoney(buyOrder, depotInvestor, transactionId)) {
            if (sellerHasEnoughStocks(sellOrder, transactionId)) {
                return true;
            } else {
                sellOrder.setStatus(TradeOrder.Status.DELETED);
                tradeOrdersContainer.addOrUpdateOrder(sellOrder, transactionId);
                System.out.println("Punishing seller for not having enough stocks for his own trade order.");
            }
        } else {
            buyOrder.setStatus(TradeOrder.Status.DELETED);
            tradeOrdersContainer.addOrUpdateOrder(buyOrder, transactionId);
            System.out.println("Punishing buyer for not having enough money for his own trade order.");
        }
        return false;
    }


    private TradeOrder findMatchingTradeOrder(TradeOrder tradeOrder, String transactionId) {
        System.out.println("Searching for matching order.");

        TradeOrder filter = new TradeOrder();
        // we are looking for a opposing order type from the same stocks (company) within the price limit
        filter.setCompany(tradeOrder.getCompany());
        if (tradeOrder.getType().equals(TradeOrder.Type.BUY_ORDER)) {
            filter.setType(TradeOrder.Type.SELL_ORDER);
        } else {
            filter.setType(TradeOrder.Type.BUY_ORDER);
        }
        filter.setPriceLimit(tradeOrder.getPriceLimit());
        filter.setStatus(TradeOrder.Status.NOT_COMPLETED);
        try {
            List<TradeOrder> matchingOrders = tradeOrdersContainer.getOrders(filter, transactionId);

            if (matchingOrders.size() > 0) {
                System.out.println("Matching trade order found!");
                // find oldest matching order
                TradeOrder oldestMatchingOrder = new TradeOrder();
                oldestMatchingOrder.setCreated(Long.MAX_VALUE);
                for (int i = 0; i < matchingOrders.size(); i++) {
                    if (matchingOrders.get(i).getCreated() < oldestMatchingOrder.getCreated()) {
                        oldestMatchingOrder = matchingOrders.get(i);
                    }
                }
                return oldestMatchingOrder;
            } else {
                System.out.println("No matching trade oder found :(");
            }
            return null;
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }
        return null;
    }


    private boolean buyerHasEnoughMoney(TradeOrder tradeOrder, DepotInvestor depotInvestor, String transactionId) throws ConnectionError {
        // check if investor has enough money to perform transaction
        StockPricesContainer stockPricesContainer = factory.newStockPricesContainer();
        double currentMarketValue = stockPricesContainer.getMarketValue(tradeOrder.getCompany(), transactionId).getPrice();
        double transactionCost = ((double) tradeOrder.getTotalAmount() * currentMarketValue) * (1.0 + PROVISION_PERCENTAGE);

        double investorBudget = depotInvestor.getBudget(transactionId);

        return investorBudget >= transactionCost;
    }


    // bei buy order ist seller die matchingTradeOrder und vice versa
    private boolean sellerHasEnoughStocks(TradeOrder tradeOrder, String transactionId) throws ConnectionError {
        if (tradeOrder.getInvestorType().equals(TradeOrder.InvestorType.COMPANY)) {
            DepotCompany depotCompany = factory.newDepotCompany(tradeOrder.getCompany(), transactionId);
            return depotCompany.getTotalAmountOfStocks(transactionId) >= (tradeOrder.getTotalAmount() - tradeOrder.getCompletedAmount());
        } else {
            DepotInvestor depotInvestor = factory.newDepotInvestor(new Investor(tradeOrder.getInvestorId()), transactionId);
            return depotInvestor.getStockAmount(tradeOrder.getCompanyId(), transactionId) >= (tradeOrder.getTotalAmount() - tradeOrder.getCompletedAmount());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
