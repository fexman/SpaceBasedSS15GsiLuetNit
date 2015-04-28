package TUI;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import Service.ConnectionError;
import Service.MarketAgentService;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by j0h1 on 28.04.2015.
 */
public class TUIMarketAgent {
    public static void main(String[] args) {

        if (args.length != 5) {
            showUsage();
        }

        int mode = 3;
        try {
            mode = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Mode is not a valid Integer.");
            System.exit(0);
        }

        IFactory factory = null;
        try{
            switch (mode) {
                case 0:
                    factory = new XvsmFactory(args[1]);
                    break;
                case 1:
                    factory = new RmiFactory(args[1]);
                    break;
                default: showUsage();
            }
        } catch (ConnectionError connectionError) {
            System.out.println("Error while connecting: " + connectionError.getMessage());
            connectionError.printStackTrace();
            System.exit(0);
        }

        String marketAgentId = args[2];

        Integer interventionTime = 2000;
        try {
            interventionTime = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Intervention time is not a valid Integer.");
            System.exit(0);
        }

        Double maxFluctuation = 0.0;
        try {
            maxFluctuation = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            System.out.println("Intervention time is not a valid Double.");
            System.exit(0);
        }

        final MarketAgentService marketAgentService = new MarketAgentService(marketAgentId, factory);

        Timer marketAnalysisTimer = new Timer();
        // scheduling task to perform market analysis, starting after inventionTime milliseconds (second parameter),
        // repeating every inventionTime milliseconds (last parameteter of timer)
        marketAnalysisTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("MarketAgent: Performing market analysis.");
                    marketAgentService.performMarketAnalysis();
                } catch (ConnectionError connectionError) {
                    connectionError.printStackTrace();
                }
            }
        }, interventionTime, interventionTime);

        //TODO taken out fluctuation generation -> throws error -> have to evaluate why

//        Timer priceFluctuationTimer = new Timer();
//        final Double finalMaxFluctuation = maxFluctuation;
//        // scheduling task to perform add a price fluctuation to a random market value, starting after
//        // 3 x inventionTime milliseconds, repeating every 3 x inventionTime milliseconds
//        priceFluctuationTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("MarketAgent: Adding price fluctuation.");
//                    marketAgentService.addPriceFluctuation(finalMaxFluctuation);
//                } catch (ConnectionError connectionError) {
//                    connectionError.printStackTrace();
//                }
//            }
//        }, 3 * interventionTime, 3 * interventionTime);

        System.out.println("MarketAgent active. Press any key at any time to shutdown.");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        marketAnalysisTimer.cancel();
//        priceFluctuationTimer.cancel();

        factory.destroy();

    }


    public static void showUsage() {
        System.out.println("Usage: <mode> <adress> <marketAgentId> <interventionTimer> <maxFluctuation>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }

}
