package Service;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.FondsIndexContainer;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.*;
import Util.TransactionTimeout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by j0h1 on 28.04.2015.
 */
public class MarketAgentService extends Service {

    private String id;
    private TradeOrderContainer tradeOrdersContainer;
    private StockPricesContainer stockPricesContainer;

    public MarketAgentService(String id, IFactory factory) {
        super(factory);
        this.id = id;
        tradeOrdersContainer = factory.newTradeOrdersContainer();
        stockPricesContainer = factory.newStockPricesContainer();
    }

    public synchronized void performMarketAnalysis() throws ConnectionErrorException {
        String transactionId = "";
        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            List<MarketValue> companies = stockPricesContainer.getCompanies(transactionId);
            List<MarketValue> fonds = stockPricesContainer.getFonds(transactionId); //To seperate fonds. Their prices are calculated after companies.
            List<MarketValue> updatedCompanyStockPrices = new ArrayList<>(); //To save bandwith when calculating fond price
            for (MarketValue marketValue : companies) {
                // get all open/partially completed buy orders of current market value
                TradeOrder buyOrdersFilter = new TradeOrder();
                buyOrdersFilter.setTradeObjectId(marketValue.getId());
                buyOrdersFilter.setType(TradeOrder.Type.BUY_ORDER);
                buyOrdersFilter.setStatus(TradeOrder.Status.NOT_COMPLETED);

                System.out.println("Processing company: " + marketValue.getId());
                List<TradeOrder> buyOrders = tradeOrdersContainer.getOrders(buyOrdersFilter, transactionId);
                int stockDemand = 0;
                // demand of current stock in circulation (Vk in specification)
                for (TradeOrder buyOrder : buyOrders) {
                    stockDemand += buyOrder.getPendingAmount();
                }
                System.out.println("\tFound " + buyOrders.size() + " buy orders with demand " + stockDemand + ".");

                // get all open/partially completed sell orders of current market value
                TradeOrder sellOrdersFilter = new TradeOrder();
                sellOrdersFilter.setTradeObjectId(marketValue.getId());
                sellOrdersFilter.setType(TradeOrder.Type.SELL_ORDER);
                sellOrdersFilter.setStatus(TradeOrder.Status.NOT_COMPLETED);

                List<TradeOrder> sellOrders = tradeOrdersContainer.getOrders(sellOrdersFilter, transactionId);
                int stockSupply = 0;
                // supply of current stock in circulation (Vv in specification)
                for (TradeOrder sellOrder : sellOrders) {
                    stockSupply += sellOrder.getPendingAmount();
                }
                System.out.println("\tFound " + sellOrders.size() + " sell orders with supply " + stockSupply + ".");


                double currentStockPrice = marketValue.getPrice();

                // calculate new stock price
                double newStockPrice = Math.max(1, currentStockPrice + ((stockDemand - stockSupply) / Math.max(1, stockDemand + stockSupply)) * 0.0625);

                // write new stock price to stock price container
                marketValue.setPrice(newStockPrice);
                stockPricesContainer.addOrUpdateMarketValue(marketValue, transactionId);

                //For fonds (see below)
                updatedCompanyStockPrices.add(marketValue);

                System.out.println("\tUpdated company " + marketValue.getId() + "'s market value from " + currentStockPrice + " to " + newStockPrice);
            }

            //For each fond ..
            for (MarketValue mw : fonds) {

                System.out.println("Processing fond: "+mw.getId());

                double budgetFactor = 0d; //Budget part of the fonds price
                double stockFactor = 0d; //Stock part
                double oldPrice = mw.getPrice(); //just for prints

                Investor investor = new Investor(mw.getId());
                investor.setFonds(true);

                FondsIndexContainer fondsIndexContainer = factory.newFondsIndexContainer();
                List<AddressInfo> remoteMarkets =  fondsIndexContainer.getMarkets(investor,transactionId);

                //For each market where the fond is registred ...
                for (AddressInfo addressInfo : remoteMarkets) {
                    System.out.println("\tCurrent market: "+addressInfo);

                    IFactory currentFactory;
                    List<MarketValue> companyStockPrices;
                    DepotInvestor depotInvestor;
                    String subTransactionId;

                    //Use local data OR connect to remote market
                    if (factory.getAddressInfo().equals(addressInfo)) { //LOCAL
                        System.out.println("\tIs local market, using cached data.");
                        currentFactory = factory;
                        subTransactionId = transactionId; //Just a local transaction
                        companyStockPrices = updatedCompanyStockPrices; //As calculated above
                    } else { //REMOTE
                        System.out.println("\tIs remote market, querying data with new sub-transaction.");
                        if (addressInfo.getProtocol().equals(AddressInfo.Protocol.XVSM)) {
                            currentFactory = new XvsmFactory(addressInfo.getAddress());
                        } else {
                            currentFactory = new RmiFactory(addressInfo.getAddress());
                        }
                        subTransactionId = currentFactory.createTransaction(TransactionTimeout.DEFAULT);
                        companyStockPrices = currentFactory.newStockPricesContainer().getCompanies(subTransactionId);
                    }

                    depotInvestor = currentFactory.newDepotInvestor(investor,subTransactionId);

                    //Calculate budget-factor contribution of current market
                    double budgetFactorContribution = depotInvestor.getBudget(subTransactionId)/mw.getTradeVolume();
                    budgetFactor += budgetFactorContribution;
                    System.out.println("\t\tCurrent budget-factor contribution: "+budgetFactorContribution);

                    //Calculate stock-factor contribution of current market by iterating over companies of this market
                    for (MarketValue companyMw : companyStockPrices) {
                        int amount = depotInvestor.getTradeObjectAmount(companyMw.getId(),subTransactionId);
                        double stockFactorContribution = (amount*companyMw.getPrice())/mw.getTradeVolume();
                        System.out.println("\t\tStocks by "+companyMw.getId()+" found "+amount+"x result in: "+stockFactorContribution);
                        stockFactor += stockFactorContribution;
                    }

                    //If subTransactionId is remote, commit changes remotely
                    if (subTransactionId != transactionId) {
                        currentFactory.commitTransaction(subTransactionId);
                    }

                }

                //write results
                mw.setPrice(budgetFactor+stockFactor);
                System.out.println("\tUpdated fond " + mw.getId() + "'s market value from "+oldPrice+" to " + mw.getPrice());
                System.out.println("\tfrom Budget: "+budgetFactor+" | from stocks: "+stockFactor+" | tradeVolume: "+mw.getTradeVolume());
                stockPricesContainer.addOrUpdateMarketValue(mw, transactionId);

            }
            factory.commitTransaction(transactionId);

        } catch (ConnectionErrorException e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw new ConnectionErrorException(e);
            } catch (ConnectionErrorException ex) {
                throw new ConnectionErrorException(ex);
            }
        }
    }

    public synchronized void addPriceFluctuation(double maxFluctuation) throws ConnectionErrorException {
        String transactionId = "";
        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            List<MarketValue> companies = stockPricesContainer.getCompanies(transactionId);
            if (companies.size() > 0) {
                Random rand = new Random();

                int randomMarketValueIndex = 0;
                if (companies.size() > 1) {
                    // calculate random number between 0 and marketValues.size() - 1
                    randomMarketValueIndex = rand.nextInt(companies.size());
                }

                MarketValue randomMarketValue = companies.get(randomMarketValueIndex);
                System.out.println("Processing company: " + randomMarketValue.getId());

                // this calculates a random number between -maxFluctuation and +maxFluctuation
                double randomFluctuation = -maxFluctuation + (2 * maxFluctuation) * rand.nextDouble();

                // calculating the new market value price, considering a random fluctuation within a specified range
                double newMarketValuePrice = randomMarketValue.getPrice() + (randomMarketValue.getPrice() * randomFluctuation);

                double oldMarketValuePrice = randomMarketValue.getPrice();
                randomMarketValue.setPrice(newMarketValuePrice);

                stockPricesContainer.addOrUpdateMarketValue(randomMarketValue, transactionId);
                System.out.println("\tUpdated company " + randomMarketValue.getId() + "'s market value from " + oldMarketValuePrice + " to " + newMarketValuePrice+"by random fluctation: "+randomFluctuation);

                //Update corresponding fonds
                for (MarketValue mw: stockPricesContainer.getFonds(transactionId)) {
                    Investor investor = new Investor(mw.getId());
                    investor.setFonds(true);
                    DepotInvestor depotInvestor = factory.newDepotInvestor(investor,transactionId);

                    int randomCompanyStockAmount = depotInvestor.getTradeObjectAmount(randomMarketValue.getId(),transactionId);
                    if (randomCompanyStockAmount > 0) {
                        System.out.println("Processing fond :" + investor.getId() + " owning " + randomCompanyStockAmount + " " + randomMarketValue.getId() + "-stocks!");
                        double newPrice = mw.getPrice() - (randomCompanyStockAmount*oldMarketValuePrice)/mw.getTradeVolume() + (randomCompanyStockAmount*newMarketValuePrice)/mw.getTradeVolume();
                        System.out.println("\tUpdated fonds " + investor.getId() + "'s market value from " + mw.getPrice() + " to " + newPrice + " according to fluctatet market value.");
                        mw.setPrice(newPrice);
                        stockPricesContainer.addOrUpdateMarketValue(mw, transactionId);

                    }
                }

                factory.commitTransaction(transactionId);
            }
        } catch (ConnectionErrorException e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw new ConnectionErrorException(e);
            } catch (ConnectionErrorException ex) {
                throw new ConnectionErrorException(ex);
            }
        }
    }

    public String getId() {
        return id;
    }
}
