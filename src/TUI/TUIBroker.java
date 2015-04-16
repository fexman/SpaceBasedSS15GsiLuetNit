package TUI;

import Factory.IFactory;
import Factory.XvsmFactory;
import Service.Broker;
import Service.ConnectionError;

import java.io.IOException;

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
        IFactory factory = null;
        try{
            switch (mode) {
                case 0:
                    factory = new XvsmFactory(args[1]);
                    break;
                case 1:
                    factory = null;//TODO: RMI FACTORY
                    break;
                default: showUsage();
            }
        } catch (ConnectionError connectionError) {
            System.out.println("Error while connecting: "+connectionError.getMessage());
        }

        //Issue stocks
        Broker broker = new Broker(factory);
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
        //Terminate connection
        factory.destroy();
        //
        // System.exit(0);

    }

    public static void showUsage() {
        System.out.println("Usage: <mode> <adress>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }
}
