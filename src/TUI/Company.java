package TUI;

import Model.IssueStockRequest;
import Model.Stock;
import SInterface.ConnectionError;
import SInterface.ICompany;
import SXvsm.XvsmCompany;
import SXvsm.XvsmUtil;
import org.mozartspaces.core.MzsCoreException;

/**
 * Created by Felix on 06.04.2015.
 */
public class Company {
    public static void main(String[] args) {
        if (args.length != 5) {
            showUsage();
        }
        int mode = 3;
        try {
            mode = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            showUsage();
        }

        Integer amount = null;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            showUsage();
        }

        Double price = null;
        try {
            price = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            showUsage();
        }

        ICompany comp = null;
        switch (mode) {
            case 0:
                try {
                    comp = new XvsmCompany(XvsmUtil.initConnection(args[1]));
                    issueStocks(comp, new IssueStockRequest(args[2],amount,price));
                    XvsmUtil.getXvsmConnection().getCapi().shutdown(XvsmUtil.getXvsmConnection().getSpace());
                } catch (MzsCoreException e) {
                    System.out.println("XVSM-connection could not be established: "+e.getMessage());
                }
                break;
            case 1:
                //TODO: RMI SERVICE
                issueStocks(comp, new IssueStockRequest(args[2],amount,price));
                break;
            default: showUsage(); break;
        }




    }

    public static void issueStocks(ICompany comp, IssueStockRequest isr) {
        try {
            comp.issueStocks(isr);
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }
    }

    public static void showUsage() {
        System.out.println("Usage: <mode> <adress> <companyId> <stockAmount> <price>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }
}

