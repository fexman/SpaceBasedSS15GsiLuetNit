package Service;

import Factory.IFactory;
import MarketEntities.DepotCompany;
import MarketEntities.ISRContainer;
import Model.IssueStockRequest;
import Util.TransactionTimeout;

/**
 * Created by Felix on 06.04.2015.
 */
public class CompanyService extends Service {

    public CompanyService(IFactory factory) {
        super(factory);
    }

    public void issueStocks(IssueStockRequest isr) throws ConnectionError {

        //create TransactionId
        String transactionId = null;

        try {

            //Init transaction
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            //Create containers
            DepotCompany depotCompany = factory.newDepotCompany(isr.getCompany(), transactionId);
            ISRContainer isrContainer = factory.newISRContainer();

            //Write to company-depot
            System.out.print("Writing new stocks to depot ... ");
            depotCompany.addStocks(isr.toStocks(),transactionId);
            System.out.println("done.");

            //Issue Stocks
            System.out.print("Writing IS-request to container ... ");
            isrContainer.addIssueStocksRequest(isr, transactionId);
            System.out.println("done.");

            factory.commitTransaction(transactionId);
            System.out.println("Commited: " + isr);

        } catch (ConnectionError e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw e;
            } catch (ConnectionError ex) {
                throw ex;
            }
        }
    }
}
