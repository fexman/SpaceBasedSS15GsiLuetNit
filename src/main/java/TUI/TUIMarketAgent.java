package TUI;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import Service.ConnectionErrorException;
import Service.MarketAgentService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
        } catch (ConnectionErrorException connectionErrorException) {
            System.out.println("Error while connecting: " + connectionErrorException.getMessage());
            connectionErrorException.printStackTrace();
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

        // schedule timer task to perform market manipulation operations
        final Timer operationTimer = new Timer();
        operationTimer.scheduleAtFixedRate(new OperationTimerTask(factory, marketAgentId, maxFluctuation), interventionTime, interventionTime);

        System.out.println("MarketAgent active. Press any key at any time to shutdown.");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        operationTimer.cancel();

        factory.destroy();
    }


    public static void showUsage() {
        System.out.println("Usage: <mode> <adress> <marketAgentId> <interventionTimer> <maxFluctuation>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }

    static class OperationTimerTask extends TimerTask {
        private MarketAgentService marketAgentService;
        private double maxFluctuation;
        private int operationCounter;

        public OperationTimerTask(IFactory factory, String marketAgentId, double maxFluctuation) {
            this.maxFluctuation = maxFluctuation;
            marketAgentService = new MarketAgentService(marketAgentId, factory);
            operationCounter = 0;
        }

        @Override
        public void run() {
            try {
                operationCounter++;
                System.out.println("\n+++ MarketAgent: Performing market analysis. +++\n");
                marketAgentService.performMarketAnalysis();
                // on every third operation call, additionally add a random price fluctuation
                if (operationCounter != 0 && operationCounter % 3 == 0) {
                    System.out.println("\n+++ MarketAgent: Adding price fluctuation. +++\n");
                    marketAgentService.addPriceFluctuation(maxFluctuation);
                }
            } catch (ConnectionErrorException connectionErrorException) {
                connectionErrorException.printStackTrace();
            }
        }
    }

}
