package TUI;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import Model.Company;
import Service.CompanyService;
import Service.ConnectionErrorException;

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
            System.out.println("Error while connecting: "+ connectionErrorException.getMessage());
            connectionErrorException.printStackTrace();
            System.exit(0);
        }

        //Issue stocks
        CompanyService compService = new CompanyService(factory);
        try {
            compService.issueStocks(comp.createIssueStockRequest(amount,price));
        } catch (ConnectionErrorException connectionErrorException) {
            System.out.println("Error while issuing stocks: " + connectionErrorException.getMessage());
        }

        //Terminate connection
        factory.destroy();

    }

    public static void showUsage() {
        System.out.println("Usage: <mode> <adress> <companyId> <stockAmount> <price>");
        System.out.println("\tmode: 0 - XVSM");
        System.out.println("\tmode: 1 - RMI");
        System.exit(0);
    }
}

