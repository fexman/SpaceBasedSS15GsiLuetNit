package TUI;

import XvsmServer.XvsmServer;

/**
 * Created by Felix on 05.05.2015.
 */
public class TUIXvsmServer {

    public static void main(String[] args) {

        //Input checking
        if (args.length != 1) {
            showUsage();
        }
        new Thread(new XvsmServer(args[0])).start();

    }

    public static void showUsage() {
        System.out.println("Usage: <xsvm-uri>");
        System.exit(0);
    }
}
