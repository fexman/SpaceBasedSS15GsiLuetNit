package Service;

import Factory.IFactory;
import MarketEntities.IssueStockRequestContainer;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.IssueStockRequest;
import Model.MarketValue;
import Model.TradeOrder;
import MarketEntities.Subscribing.IssueStockRequests.IISRRequestSub;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;

import java.util.List;

/**
 * Created by Felix on 11.04.2015.
 */
public class BrokerService extends Service implements IISRRequestSub, ITradeOrderSub{

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
    public void pushNewTradeOrders(List<TradeOrder> data) {
        System.out.println("Trade Orders Callback.");
    }

    @Override
    public void pushNewISRs(List<IssueStockRequest> newISRs) {
        try {
            takeAndProcessISRs();
        } catch (ConnectionError connectionError) {
            System.out.println("FATAL ERROR: Connection Error on subscription-PUSH.");
        }
    }
}
