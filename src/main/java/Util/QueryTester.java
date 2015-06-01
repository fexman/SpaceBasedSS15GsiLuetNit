package Util;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 01.06.2015.
 */
public class QueryTester {

    public static IFactory factory;
    public static boolean ALT_MODE = false;

    public static void main(String args[]) throws Exception {
        factory = QueryTesterFactory.getFactory();

        TradeOrder to = new TradeOrder();
        to.setPrioritized(null);

        TradeOrderContainer toc = factory.newTradeOrdersContainer();
        List<TradeOrder> results = toc.getOrders(to, null);
        System.out.println();
        System.out.println(" ++++ Query Results +++ ");
        for (TradeOrder toResult: results) {
            System.out.println("\t"+toResult);
        }
        System.out.println(" ++++ Results End +++ ");

        System.exit(0);
    }

    private static class QueryTesterFactory {

        private static final String CONN_URI = "xvsm://localhost:12345";
        private static final String CONN_URI_ALT = "localhost:12345";

        public static IFactory getFactory() throws ConnectionErrorException {
            if (ALT_MODE) {
                return new RmiFactory(CONN_URI_ALT);
            } else {
                return new XvsmFactory(CONN_URI);
            }
        }

    }
}
