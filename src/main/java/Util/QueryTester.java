package Util;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.DepotInvestor;
import MarketEntities.StockPricesContainer;
import MarketEntities.TradeOrderContainer;
import Model.Investor;
import Model.MarketValue;
import Model.TradeObject;
import Model.TradeOrder;
import Service.ConnectionErrorException;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Felix on 01.06.2015.
 */
public class QueryTester {

    public static IFactory factory;
    public static final boolean  ALT_MODE = false;

    public static void main(String args[]) throws Exception {
        factory = QueryTesterFactory.getFactory();

        // +++ +++ +++ +++ +++ +++ +++ TRADEORDER TESTS  +++ +++ +++ +++ +++ +++ +++
        /*TradeOrder to = new TradeOrder();
        to.setTradeObjectId("FROST");
        to.setPriceLimit(4d);
        to.setStatus(TradeOrder.Status.NOT_COMPLETED);
        to.setType(TradeOrder.Type.SELL_ORDER);
        //Investor investor = new Investor("dudette");
        //to.setInvestor(investor);

        System.out.print("Filter: "+to);

        TradeOrderContainer toc = factory.newTradeOrdersContainer();
        List<TradeOrder> results = toc.getOrders(to, null);
        System.out.println();
        System.out.println(" ++++ Query Results +++ ");
        for (TradeOrder toResult: results) {
            System.out.println("\t"+toResult);
        }
        System.out.println(" ++++ Results End +++ ");*/

        // +++ +++ +++ +++ +++ +++ +++ INVESTOR TESTS  +++ +++ +++ +++ +++ +++ +++
        Investor inv = new Investor("dudette");
        inv.setFonds(true);

        DepotInvestor di = factory.newDepotInvestor(inv,null);

        List<TradeObject> results = di.readAllTradeObjects(null);
        System.out.println();
        System.out.println(" ++++ Query Results +++ ");
        HashMap<String, Integer> resultCount = new HashMap<>();
        for (TradeObject to : results) {
            if (!resultCount.containsKey(to.toString())){
                resultCount.put(to.toString(),1);
            } else {
                resultCount.put(to.toString(),resultCount.get(to.toString())+1);
            }
        }
        for (String s: resultCount.keySet()) {
            System.out.println("\t"+resultCount.get(s)+"x "+s);
        }
        System.out.println(" ++++ Results End +++ ");

        // +++ +++ +++ +++ +++ +++ +++ MARKETVALUE TESTS  +++ +++ +++ +++ +++ +++ +++
        /*StockPricesContainer sps = factory.newStockPricesContainer();

        List<MarketValue> results = sps.getCompanies(null);
        System.out.println();
        System.out.println(" ++++ Query Results +++ ");
        for (MarketValue mw : results) {
            System.out.println("\t"+mw);
        }
        System.out.println(" ++++ Results End +++ ");*/

        System.exit(0);
    }

    private static class QueryTesterFactory {

        private static final String CONN_URI = "xvsm://localhost:12345";
        private static final String CONN_URI_ALT = "localhost:12346";

        public static IFactory getFactory() throws ConnectionErrorException {
            if (ALT_MODE) {
                System.out.println("ALT_MODE");
                return new RmiFactory(CONN_URI_ALT);
            } else {
                System.out.println("NORMAL_MODE");
                return new XvsmFactory(CONN_URI);
            }
        }

    }
}
