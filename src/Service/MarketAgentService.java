package Service;

import Factory.IFactory;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.MarketValue;
import Model.TradeOrder;
import Util.TransactionTimeout;

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

    public synchronized void performMarketAnalysis() throws ConnectionError {
        String transactionId = "";
        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            List<MarketValue> marketValues = stockPricesContainer.getAll(transactionId);
            for (MarketValue marketValue : marketValues) {
                // get all open/partially completed buy orders of current market value
                TradeOrder buyOrdersFilter = new TradeOrder();
                buyOrdersFilter.setCompany(marketValue.getCompany());
                buyOrdersFilter.setType(TradeOrder.Type.BUY_ORDER);
                buyOrdersFilter.setStatus(TradeOrder.Status.NOT_COMPLETED);

                List<TradeOrder> buyOrders = tradeOrdersContainer.getOrders(buyOrdersFilter, transactionId);
                int stockDemand = 0;
                // demand of current stock in circulation (Vk in specification)
                for (TradeOrder buyOrder : buyOrders) {
                    stockDemand += buyOrder.getPendingAmount();
                }

                // get all open/partially completed sell orders of current market value
                TradeOrder sellOrdersFilter = new TradeOrder();
                sellOrdersFilter.setCompany(marketValue.getCompany());
                sellOrdersFilter.setType(TradeOrder.Type.SELL_ORDER);
                sellOrdersFilter.setStatus(TradeOrder.Status.NOT_COMPLETED);

                List<TradeOrder> sellOrders = tradeOrdersContainer.getOrders(sellOrdersFilter, transactionId);
                int stockSupply = 0;
                // supply of current stock in circulation (Vv in specification)
                for (TradeOrder sellOrder : sellOrders) {
                    stockSupply += sellOrder.getPendingAmount();
                }

                double currentStockPrice = marketValue.getPrice();

                // calculate new stock price
                double newStockPrice = Math.max(1, currentStockPrice + ((stockDemand - stockSupply) / Math.max(1, stockDemand + stockSupply)) * 0.0625);

                // write new stock price to stock price container
                marketValue.setPrice(newStockPrice);
                stockPricesContainer.addOrUpdateMarketValue(marketValue, transactionId);
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

    public synchronized void addPriceFluctuation(double maxFluctuation) throws ConnectionError {
        String transactionId = "";
        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            List<MarketValue> marketValues = stockPricesContainer.getAll(transactionId);
            if (marketValues.size() > 0) {
                Random rand = new Random();

                int randomMarketValueIndex = 0;
                if (marketValues.size() > 1) {
                    // calculate random number between 0 and marketValues.size() - 1
                    randomMarketValueIndex = rand.nextInt(marketValues.size());
                }

                MarketValue randomMarketValue = marketValues.get(randomMarketValueIndex);

                // this calculates a random number between -maxFluctuation and +maxFluctuation
                double randomFluctuation = -maxFluctuation + (2 * maxFluctuation) * rand.nextDouble();

                // calculating the new market value price, considering a random fluctuation within a specified range
                double newMarketValuePrice = randomMarketValue.getPrice() + (randomMarketValue.getPrice() * randomFluctuation);

                randomMarketValue.setPrice(newMarketValuePrice);

                stockPricesContainer.addOrUpdateMarketValue(randomMarketValue, transactionId);
                factory.commitTransaction(transactionId);
            }
        } catch (ConnectionError e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw new ConnectionError(e);
            } catch (ConnectionError ex) {
                throw new ConnectionError(ex);
            }
        }
    }

    public String getId() {
        return id;
    }
}
