package Service;

import Factory.IFactory;
import MarketEntities.DepotCompany;
import MarketEntities.IssueRequestContainer;
import Model.IssueStockRequest;
import Util.TransactionTimeout;

/**
 * Created by Felix on 06.04.2015.
 */
public class CompanyService extends Service {

    public CompanyService(IFactory factory) {
        super(factory);
    }

    public void issueStocks(IssueStockRequest isr) throws ConnectionErrorException {

        //create TransactionId
        String transactionId = null;

        try {

            //Init transaction
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            //Create containers
            DepotCompany depotCompany = factory.newDepotCompany(isr.getCompany(), transactionId);
            IssueRequestContainer issueRequestContainer = factory.newIssueRequestContainer();

            //Write to company-depot
            System.out.print("Writing new stocks to depot ... ");
            depotCompany.addTradeObjects(isr.toTradeObjects(),transactionId);
            System.out.println("done.");

            //Issue Stocks
            System.out.print("Writing IS-request to container ... ");
            issueRequestContainer.addIssueRequest(isr, transactionId);
            System.out.println("done.");

            factory.commitTransaction(transactionId);
            System.out.println("Commited: " + isr);

        } catch (ConnectionErrorException e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw e;
            } catch (ConnectionErrorException ex) {
                throw ex;
            }
        }
    }
}
