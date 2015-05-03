package TUI;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import Service.BrokerService;
import Service.ConnectionError;

import java.io.IOException;

/**
 * Created by Felix on 12.04.2015.
 */
public class TUIBroker {
    public static void main(String[] args) {
        if (args.length != 3) {
            showUsage();
        }

        String brokerId = args[0];

        int mode = 3;
        try {
            mode = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            showUsage();
        }

        //Init connection
        IFactory factory = null;
        try{
            switch (mode) {
                case 0:
                    factory = new XvsmFactory(args[2]);
                    break;
                case 1:
                    System.out.println("Here");
                    factory = new RmiFactory(args[2]);
                    break;
                default: showUsage();
            }
        } catch (ConnectionError connectionError) {
            System.out.println("Error while connecting: "+connectionError.getMessage());
        }

        //Issue stocks
        BrokerService broker = new BrokerService(brokerId, factory);
        System.out.println("Will broke now. Press any key at any time to shutdown.");
        try {
            broker.startBroking();
        } catch (ConnectionError connectionError) {
            System.out.println("Error on startup: " + connectionError.getMessage());
        }
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        factory.destroy();
        //
        // System.exit(0);

    }

    public static void showUsage() {
        System.out.println("Usage: <id> <mode> <adress>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }
}
