package TUI;

import Model.Company;
import Service.ConnectionError;
import Service.ICompany;
import Service.XVSM.XvsmCompany;

/**
 * Created by Felix on 06.04.2015.
 */
public class TUICompany {
    public static void main(String[] args) {

        //Input checking
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

        //Init connection
        Company comp = new Company(args[2]);
        ICompany compService = null;
        try{
            switch (mode) {
                case 0:
                    compService = new XvsmCompany(args[1]);
                    break;
                case 1:
                    compService = null;//TODO: RMI SERVICE
                    break;
                default: showUsage();
            }
        } catch (ConnectionError connectionError) {
            System.out.println("Error while connecting: "+connectionError.getMessage());
        }

        //Issue stocks
        try {
            compService.issueStocks(comp.createIssueStockRequest(amount,price));
        } catch (ConnectionError connectionError) {
            System.out.println("Error while issuing stocks: " + connectionError.getMessage());
        }

        //Terminate connection
        try {
            compService.shutdown();
        } catch (ConnectionError connectionError) {
            System.out.println("Error while shutting down: "+connectionError.getMessage());
        }

    }

    public static void showUsage() {
        System.out.println("Usage: <mode> <adress> <companyId> <stockAmount> <price>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }
}

