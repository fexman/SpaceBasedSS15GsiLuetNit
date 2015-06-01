package Service;

import Factory.IFactory;
import MarketEntities.*;
import Model.*;
import Util.TransactionTimeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Felix on 11.04.2015.
 */
public class BrokerService extends Service {

    private String id;
    private ISRContainer isrContainer;
    private TradeOrderContainer tradeOrdersContainer;
    private StockPricesContainer stockPricesContainer;
    private TransactionHistoryContainer transactionHistoryContainer;
    private BrokerSupportContainer brokerSupportContainer;

    private ISRThread isrThread;
    private TradeOrderThread toThread;
    private StockPricesThread spThread;

    private final double PROVISION_PERCENTAGE = 0.03;

    public BrokerService(String id, IFactory factory) {
        super(factory);
        this.id = id;
        isrContainer = factory.newISRContainer();
        tradeOrdersContainer = factory.newTradeOrdersContainer();
        stockPricesContainer = factory.newStockPricesContainer();
        transactionHistoryContainer = factory.newTransactionHistoryContainer();
        brokerSupportContainer = factory.newBrokerSupportContainer();
        this.isrThread = new ISRThread();
        this.toThread = new TradeOrderThread();
        this.spThread = new StockPricesThread();
    }

    public void startBroking() throws ConnectionErrorException {
        new Thread(isrThread).start();
        new Thread(toThread).start();
        //new Thread(spThread).start();
    }

    private class ISRThread extends Thread {

        private boolean running;

        public ISRThread() {
            this.running = true;
        }

        @Override
        public void run() {
            while (running) {
                System.out.println("ISR: Looking for new ISRs ....");
                String transactionId = null;
                try {

                    transactionId = factory.createTransaction(TransactionTimeout.INFINITE);

                    //Blocking
                    List<IssueStockRequest> isrs = isrContainer.takeIssueStockRequests(transactionId);
                    if (isrs == null) {
                        running = false;
                        continue;
                    }

                    if (isrs.size() > 0) {
                        System.out.println("ISR: Got " + isrs.size() + " new ISRs!");
                        for (IssueStockRequest isr : isrs) {

                            //Set market Value of new stocks
                            MarketValue mw = stockPricesContainer.getMarketValue(isr.getCompany().getId(), transactionId);
                            if (mw == null) {
                                System.out.println("Setting new marketValue for " + isr.getCompany() + " on ISR-price: "+isr.getPrice());
                                mw = new MarketValue(isr.getCompany(), isr.getPrice(),isr.getAmount());
                                stockPricesContainer.addOrUpdateMarketValue(mw, transactionId);
                            } else {
                                System.out.println("Updating marketValue of " + mw.getTradeVolume() + " for "+ isr.getAmount() + " (from ISR) resulting in "+(mw.getTradeVolume() + isr.getAmount()));
                                mw.setTradeVolume(mw.getTradeVolume() + isr.getAmount());
                                mw.setPriceChanged(false);
                                stockPricesContainer.addOrUpdateMarketValue(mw, transactionId);
                            }

                            //Create Trade Order
                            TradeOrder order = new TradeOrder(isr.getCompany(), isr.getCompany().getId(), isr.getAmount(), 0d);
                            order.setJustChanged(true);
                            tradeOrdersContainer.addOrUpdateOrder(order, transactionId);
                            System.out.println("ISR: New order: "+order);
                        }
                    }
                    System.out.println("ISR: Commit.");
                    factory.commitTransaction(transactionId);

                } catch (TransactionTimeoutException e) {
                    System.out.println("ISR: timed out!");
                    factory.removeTransaction(transactionId);
                } catch (ConnectionErrorException e) {
                    try {
                        factory.rollbackTransaction(transactionId);
                        throw new ConnectionErrorException(e);
                    } catch (ConnectionErrorException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            System.out.println("ISR: Goodbye!");
        }

        public void shutdown() {
            this.running = false;
        }

    }

    private class TradeOrderThread extends Thread {

        private boolean running;

        public TradeOrderThread() {
            this.running = true;
        }

        @Override
        public void run() {
            while (running) {
                System.out.println("TO: Looking for new TOs ....");

                String transactionId = "";
                try {
                    transactionId = factory.createTransaction(TransactionTimeout.INFINITE);

                    List<TradeOrder> newTradeOrders = brokerSupportContainer.takeNewTradeOrders(transactionId);

                    System.out.println("TO: Got TO " + newTradeOrders);

                    for (TradeOrder recentlyUpdatedTradeOrder : newTradeOrders) {
                        if (recentlyUpdatedTradeOrder.getStatus().equals(TradeOrder.Status.OPEN) || recentlyUpdatedTradeOrder.getStatus().equals(TradeOrder.Status.PARTIALLY_COMPLETED)) {
                            tradeOrdersContainer.takeOrder(recentlyUpdatedTradeOrder, transactionId); //Remove since Broker will be processing it
                            solveOrder(recentlyUpdatedTradeOrder, transactionId);
                        }
                    }

                    System.out.println("TO: Commit.");
                    factory.commitTransaction(transactionId);
                }  catch (TransactionTimeoutException e) {
                    System.out.println("TO: timed out!");
                    factory.removeTransaction(transactionId);
                }  catch (ConnectionErrorException connectionErrorException) {
                    try {
                        factory.rollbackTransaction(transactionId);
                        throw new ConnectionErrorException(connectionErrorException);
                    } catch (ConnectionErrorException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        public void shutdown() {
            this.running = false;
        }
    }

    private class StockPricesThread extends Thread {

        private boolean running;

        public StockPricesThread() {
            this.running = true;
        }

        public void run() {
            while (running) {
                System.out.println("SP: Looking for changes in stock prices ....");

                String transactionId = "";
                try {
                    transactionId = factory.createTransaction(TransactionTimeout.INFINITE);

                    List<MarketValue> newStockPrices = brokerSupportContainer.takeNewStockPrices(transactionId);

                    System.out.println("SP: Got SP " + newStockPrices);

                    for (MarketValue recentlyUpdatedMarketValue : newStockPrices) {
                        TradeOrder filter = new TradeOrder();
                        filter.setTradeObjectId(recentlyUpdatedMarketValue.getId());
                        filter.setStatus(TradeOrder.Status.NOT_COMPLETED);
                        List<TradeOrder> tradeOrders = tradeOrdersContainer.getOrders(filter, transactionId);
                        for (TradeOrder to : tradeOrders) {
                            // check again, cause status could have changed during the transaction
                            // tradeOrdersContainer.getOrders(filter, transactionId) in loop definition does not work correctly (tested multiple times)
                            List<TradeOrder> updatedToList = tradeOrdersContainer.getOrders(to, transactionId);
                            if (updatedToList != null && updatedToList.size() > 0) {
                                TradeOrder updatedTo = updatedToList.get(0);
                                if (updatedTo.getStatus().equals(TradeOrder.Status.OPEN) || updatedTo.getStatus().equals(TradeOrder.Status.PARTIALLY_COMPLETED)) {
                                    System.out.println("CALLING SOLVEORDER FROM SP THREAD!");
                                    tradeOrdersContainer.takeOrder(updatedTo, transactionId);
                                    solveOrder(updatedTo, transactionId);
                                }
                            }
                        }
                    }

                    System.out.println("SP: Commit.");
                    factory.commitTransaction(transactionId);
                } catch (TransactionTimeoutException e) {
                    System.out.println("SP: timed out!");
                    factory.removeTransaction(transactionId);
                } catch (ConnectionErrorException connectionErrorException) {
                    try {
                        factory.rollbackTransaction(transactionId);
                        throw new ConnectionErrorException(connectionErrorException);
                    } catch (ConnectionErrorException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        public void shutdown() {
            this.running = false;
        }


    }

    private synchronized void solveOrder(TradeOrder tradeOrder, String transactionId) throws ConnectionErrorException {
        TradeOrder matchingTradeOrder = findMatchingTradeOrder(tradeOrder, transactionId);

        if (matchingTradeOrder != null) {
            System.out.println("solveOrder: FOUND A MATCH.");
            if (tradeOrder.getType().equals(TradeOrder.Type.BUY_ORDER)) {
                if (validateTradeOrders(tradeOrder, matchingTradeOrder, transactionId)) {
                    executeTransaction(tradeOrder, matchingTradeOrder, transactionId);
                }
            } else {
                if (validateTradeOrders(matchingTradeOrder, tradeOrder, transactionId)) {
                    executeTransaction(matchingTradeOrder, tradeOrder, transactionId);
                }
            }
        } else {
            if (tradeOrder.getInvestorType().equals(TradeOrder.InvestorType.INVESTOR)) {
                DepotInvestor depotInvestor = factory.newDepotInvestor(new Investor(tradeOrder.getInvestorId()), transactionId);
                if (tradeOrder.getType().equals(TradeOrder.Type.BUY_ORDER)) {
                    if (!buyerHasEnoughMoney(tradeOrder, depotInvestor, transactionId)) {
                        tradeOrder.setStatus(TradeOrder.Status.DELETED);
                        System.out.println("Punishing investor for not having enough money for his own buy order.");
                    }
                } else {
                    if (!sellerHasEnoughTradeObjects(tradeOrder, transactionId)) {
                        tradeOrder.setStatus(TradeOrder.Status.DELETED);
                        System.out.println("Punishing investor for not having enough stocks for his own sell order.");
                    }
                }
            }
            // no matching trade order found
            System.out.println("Solveorder: Match WAS null.");
            tradeOrder.setJustChanged(false);
            tradeOrdersContainer.addOrUpdateOrder(tradeOrder, transactionId);
        }
    }

    private void executeTransaction(TradeOrder buyOrder, TradeOrder sellOrder, String transactionId) throws ConnectionErrorException {
        // buyer can only be an investor
        DepotInvestor depotBuyer = factory.newDepotInvestor(new Investor(buyOrder.getInvestorId()), transactionId);

        Depot depotSeller;
        if (sellOrder.getInvestorType().equals(TradeOrder.InvestorType.COMPANY)) {
            depotSeller = factory.newDepotCompany(new Company(sellOrder.getInvestorId()), transactionId);
        } else {
            depotSeller = factory.newDepotInvestor(new Investor(sellOrder.getInvestorId()), transactionId);
        }

        // list of stocks to transfer from seller to buyer depot
        List<TradeObject> boughtTradeObjects;

        if (buyOrder.getPendingAmount() >= sellOrder.getPendingAmount()) {
            System.out.println("Taking all stocks (" + sellOrder.getPendingAmount() + ") from seller depot.");
            // take ALL stocks from seller depot
            boughtTradeObjects = takeStocksFromSellerDepot(depotSeller, sellOrder, sellOrder.getPendingAmount(), transactionId);

            if (buyOrder.getPendingAmount().equals(sellOrder.getPendingAmount())) {
                System.out.println("Buy order completed.");
                buyOrder.setStatus(TradeOrder.Status.COMPLETED); // should be equal to totalAmount when completed
            } else {
                System.out.println("Buy order partially completed.");
                buyOrder.setStatus(TradeOrder.Status.PARTIALLY_COMPLETED);
            }

            System.out.println("Sell order completed.");
            sellOrder.setStatus(TradeOrder.Status.COMPLETED);
        }  else {
            // take the amount required in buy order from seller depot
            boughtTradeObjects = takeStocksFromSellerDepot(depotSeller, sellOrder, buyOrder.getPendingAmount(), transactionId);

            System.out.println("Taking " + boughtTradeObjects.size() + " stocks from seller depot.");

            buyOrder.setStatus(TradeOrder.Status.COMPLETED);

            System.out.println("Sell order partially completed.");
            sellOrder.setStatus(TradeOrder.Status.PARTIALLY_COMPLETED);
        }
        buyOrder.setCompletedAmount(buyOrder.getCompletedAmount() + boughtTradeObjects.size());
        sellOrder.setCompletedAmount(sellOrder.getCompletedAmount() + boughtTradeObjects.size());

        // add stocks to buyer depot
        depotBuyer.addTradeObjects(boughtTradeObjects, transactionId);

        // caluclate budget increases/decreases of seller/buyer
        double currentMarketValue = stockPricesContainer.getMarketValue(buyOrder.getTradeObjectId(), transactionId).getPrice();
        double totalValue = boughtTradeObjects.size() * currentMarketValue;
        double provisionAmountBuyer = (buyOrder.isPrioritized()) ? 2d : 1d;
        System.out.println("BuyOrder isPrioritized? : "+buyOrder.isPrioritized()+" -> pab: "+provisionAmountBuyer);
        double provisionAmountSeller = (sellOrder.isPrioritized()) ? 2d : 1d;
        System.out.println("SellOrder isPrioritized? : "+sellOrder.isPrioritized()+" -> pab: "+provisionAmountSeller);
        double provisionBuyer = totalValue * (PROVISION_PERCENTAGE * provisionAmountBuyer);
        double provisionSeller = totalValue * (PROVISION_PERCENTAGE * provisionAmountSeller);

        System.out.println("Removing " + (totalValue + provisionBuyer) + " from " + depotBuyer.getDepotName());
        depotBuyer.addToBudget(-(totalValue + provisionBuyer), transactionId);

        if (sellOrder.getInvestorType().equals(TradeOrder.InvestorType.INVESTOR)) {
            System.out.println("Increasing sellers budget by " + totalValue);
            ((DepotInvestor) depotSeller).addToBudget(totalValue - provisionSeller, transactionId);
        }

        // write transaction to transaction history container
        System.out.println("Writing transaction " + transactionId + " to transaction history.");
        StockOwner seller;
        if (sellOrder.getInvestorType().equals(TradeOrder.InvestorType.COMPANY)) {
            seller = new Company(sellOrder.getInvestorId());
        } else {
            seller = new Investor(sellOrder.getInvestorId());
        }

        transactionHistoryContainer.addHistoryEntry(new HistoryEntry(transactionId, id, new Investor(buyOrder.getInvestorId()), seller, buyOrder.getTradeObjectId(),
                buyOrder.getId(), sellOrder.getId(), currentMarketValue, boughtTradeObjects.size(), totalValue + provisionBuyer, provisionBuyer), transactionId);

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


    private List<TradeObject> takeStocksFromSellerDepot(Depot depotSeller, TradeOrder sellOrder, int amount, String transactionId) throws ConnectionErrorException {
        if (sellOrder.getInvestorType().equals(TradeOrder.InvestorType.COMPANY)) {
            List<TradeObject> result = new ArrayList<>();
            for (Stock s : ((DepotCompany) depotSeller).takeStocks(amount, transactionId)) {
                result.add(s);
            }
            return result;
        } else{
            return ((DepotInvestor) depotSeller).takeTradeObjects(sellOrder.getTradeObjectId(), amount, transactionId);
        }
    }


    private boolean validateTradeOrders(TradeOrder buyOrder, TradeOrder sellOrder, String transactionId) throws ConnectionErrorException {
        DepotInvestor depotInvestor = factory.newDepotInvestor(new Investor(buyOrder.getInvestorId()), transactionId);
        if (buyerHasEnoughMoney(buyOrder, depotInvestor, transactionId)) {
            System.out.println("Buyer has enough money");
            if (sellerHasEnoughTradeObjects(sellOrder, transactionId)) {
                System.out.println("Seller has enough stocks");
                return true;
            } else {
                sellOrder.setStatus(TradeOrder.Status.DELETED);
                sellOrder.setJustChanged(false);
                tradeOrdersContainer.addOrUpdateOrder(sellOrder, transactionId);
                System.out.println("Punishing seller for not having enough stocks for his own trade order.");
            }
        } else {
            buyOrder.setStatus(TradeOrder.Status.DELETED);
            buyOrder.setJustChanged(false);
            tradeOrdersContainer.addOrUpdateOrder(buyOrder, transactionId);
            System.out.println("Punishing buyer for not having enough money for his own trade order.");
        }
        return false;
    }


    private TradeOrder findMatchingTradeOrder(TradeOrder tradeOrder, String transactionId) {


        TradeOrder filter = new TradeOrder();
        // we are looking for a opposing order type from the same stocks (company) within the price limit
        filter.setTradeObjectId(tradeOrder.getTradeObjectId());
        if (tradeOrder.getType().equals(TradeOrder.Type.BUY_ORDER)) {
            filter.setType(TradeOrder.Type.SELL_ORDER);
        } else {
            filter.setType(TradeOrder.Type.BUY_ORDER);
        }
        filter.setPriceLimit(tradeOrder.getPriceLimit());
        filter.setStatus(TradeOrder.Status.NOT_COMPLETED);

        System.out.println("Searching for matching order, using filter: " + filter);

        try {

            filter.setPrioritized(true); //First get prioritized matching criteria ...
            List<TradeOrder> matchingOrders = tradeOrdersContainer.getOrders(filter, transactionId);
            Collections.sort(matchingOrders, new Comparator<TradeOrder>() { //Sort by creation date
                @Override
                public int compare(TradeOrder tradeOrder1, TradeOrder tradeOrder2) {
                    return tradeOrder1.getCreated().compareTo(tradeOrder2.getCreated());
                }
            });
            filter.setPrioritized(false); //... then not prioritized ...
            List<TradeOrder> matchingOrders2 = tradeOrdersContainer.getOrders(filter, transactionId);
            Collections.sort(matchingOrders2, new Comparator<TradeOrder>() { //Sort by creation date
                @Override
                public int compare(TradeOrder tradeOrder1, TradeOrder tradeOrder2) {
                    return tradeOrder1.getCreated().compareTo(tradeOrder2.getCreated());
                }
            });
            matchingOrders.addAll(matchingOrders2); //... and combine both lists.

            if (matchingOrders.size() > 0) {
                System.out.println(matchingOrders.size() + " matching trade orders found!");

                // get current stock price
                double currentStockPrice = stockPricesContainer.getMarketValue(tradeOrder.getTradeObjectId(), transactionId).getPrice();

                List<TradeOrder> compatibleMatchingTradeOrders = new ArrayList<>();

                // filter for price compatibility
                for (int i = 0; i < matchingOrders.size(); i++) {
                    boolean compatibleWithMarketValue;
                    if (tradeOrder.getType().equals(TradeOrder.Type.BUY_ORDER)) {
                        compatibleWithMarketValue = checkCompatibilityWithMarketValue(tradeOrder, matchingOrders.get(i), currentStockPrice, transactionId);
                    } else {
                        compatibleWithMarketValue = checkCompatibilityWithMarketValue(matchingOrders.get(i), tradeOrder, currentStockPrice, transactionId);
                    }
                    if (compatibleWithMarketValue) {
                        // trade order is compatible with current market value -> 3rd condition valid -> add them to the list
                        compatibleMatchingTradeOrders.add(matchingOrders.get(i));
                    }
                }
                System.out.println(compatibleMatchingTradeOrders.size() + "/" + matchingOrders.size() + " matching trade orders are compatible with the current stock price:");


                for (int i = 0; i < compatibleMatchingTradeOrders.size(); i++) {
                    System.out.println("\t"+compatibleMatchingTradeOrders.get(i));
                    System.out.println();
                }

                if (compatibleMatchingTradeOrders.size() > 0) {
                    for (int i = 0; i < compatibleMatchingTradeOrders.size(); i++) {
                        TradeOrder finalMatch = tradeOrdersContainer.takeOrder(compatibleMatchingTradeOrders.get(i), transactionId);
                        System.out.println("Final match #"+i+": ");
                        if (finalMatch != null) {
                            System.out.println("Found final match: " + finalMatch);
                            return finalMatch;
                        } else {
                            System.out.println("FINAL MATCH #" + i + " IS NOT IN TO CONTAINER ANYMORE!");
                        }
                    }
                } else {
                    return null;
                }
            } else {
                System.out.println("No matching trade oder found :(");
            }
            return null;
        } catch (ConnectionErrorException connectionErrorException) {
            connectionErrorException.printStackTrace();
        }
        return null;
    }


    private boolean checkCompatibilityWithMarketValue(TradeOrder buyOrder, TradeOrder sellOrder, double currentStockPrice, String transactionId) throws ConnectionErrorException {
        // current stock price must higher or equal to the minimum selling price and lower than the maximum buying price
        if (currentStockPrice >= sellOrder.getPriceLimit() && currentStockPrice <= buyOrder.getPriceLimit()) {
            return true;
        }
        return false;
    }


    private boolean buyerHasEnoughMoney(TradeOrder tradeOrder, DepotInvestor depotInvestor, String transactionId) throws ConnectionErrorException {
        // check if investor has enough money to perform transaction
        double currentMarketValue = stockPricesContainer.getMarketValue(tradeOrder.getTradeObjectId(), transactionId).getPrice();
        double provisonAmount = (tradeOrder.isPrioritized()) ? 2d : 1d;

        double transactionCost = ((double) tradeOrder.getTotalAmount() * currentMarketValue) * (1.0 + (PROVISION_PERCENTAGE * provisonAmount));

        double investorBudget = depotInvestor.getBudget(transactionId);

        return investorBudget >= transactionCost;
    }


    // bei buy order ist seller die matchingTradeOrder und vice versa
    private boolean sellerHasEnoughTradeObjects(TradeOrder tradeOrder, String transactionId) throws ConnectionErrorException {
        if (tradeOrder.getInvestorType().equals(TradeOrder.InvestorType.COMPANY)) {
            DepotCompany depotCompany = factory.newDepotCompany(new Company(tradeOrder.getInvestorId()), transactionId);
            System.out.println("TOTAL AMOUNT OF STOCKS FROM SELLER: " + depotCompany.getTotalAmountOfTradeObjects(transactionId));
            return depotCompany.getTotalAmountOfTradeObjects(transactionId) >= (tradeOrder.getTotalAmount() - tradeOrder.getCompletedAmount());
        } else {
            DepotInvestor depotInvestor = factory.newDepotInvestor(new Investor(tradeOrder.getInvestorId()), transactionId);
            return depotInvestor.getTradeObjectAmount(tradeOrder.getTradeObjectId(), transactionId) >= (tradeOrder.getTotalAmount() - tradeOrder.getCompletedAmount());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
