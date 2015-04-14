package TUI;

import Service.ConnectionError;
import Service.IBroker;
import Service.XVSM.XvsmBroker;

/**
 * Created by Felix on 12.04.2015.
 */
public class TUIBroker {
    public static void main(String[] args) {
        if (args.length != 2) {
            showUsage();
        }

        int mode = 3;
        try {
            mode = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            showUsage();
        }

        //Init connection
        IBroker brokerService = null;
        try{
            switch (mode) {
                case 0:
                    brokerService = new XvsmBroker(args[1]);
                    break;
                case 1:
                    brokerService = null;//TODO: RMI SERVICE
                    break;
                default: showUsage();
            }
        } catch (ConnectionError connectionError) {
            System.out.println("Error while connecting: "+connectionError.getMessage());
        }

        //Issue stocks
        try {
            brokerService.startBroking();
        } catch (ConnectionError connectionError) {
            System.out.println("Error on startup: " + connectionError.getMessage());
        }

        //Terminate connection
        try {
            brokerService.shutdown();
        } catch (ConnectionError connectionError) {
            System.out.println("Error while shutting down: " + connectionError.getMessage());
        }

    }

    public static void showUsage() {
        System.out.println("Usage: <mode> <adress>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }
}
