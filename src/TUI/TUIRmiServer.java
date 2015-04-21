package TUI;

import Factory.IFactory;
import Factory.XvsmFactory;
import Model.Company;
import RMIServer.RmiServer;
import Service.CompanyService;
import Service.ConnectionError;

/**
 * Created by Felix on 21.04.2015.
 */
public class TUIRmiServer {

    public static void main(String[] args) {

        //Input checking
        if (args.length != 1) {
            showUsage();
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            showUsage();
        }

        new RmiServer(port).run();

    }

    public static void showUsage() {
        System.out.println("Usage: <port>");
        System.exit(0);
    }
}
