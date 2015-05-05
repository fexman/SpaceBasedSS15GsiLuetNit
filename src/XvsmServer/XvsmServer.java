package XVSMServer;

import Util.XvsmUtil;
import org.mozartspaces.core.MzsCoreException;

import java.util.Scanner;

/**
 * Created by Felix on 05.05.2015.
 */
public class XvsmServer extends Thread {

    private String uri;

    public XvsmServer(String uri) {
        this.uri = uri;
    }

    public void run() {
        try {
            XvsmUtil.initConnection(uri, true);
            Scanner scan = new Scanner(System.in);
            System.out.println("Hit <enter> to shutdown!");
            scan.nextLine();
            System.exit(0);
        } catch (MzsCoreException e) {
            System.out.println("Was not able to create space! :(");
            e.printStackTrace();
        }

    }
}
