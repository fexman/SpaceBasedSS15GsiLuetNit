package Service.XVSM;

import MarketEntities.DepotCompany;
import MarketEntities.ISRContainer;
import MarketEntities.XVSM.XvsmDepotCompany;
import MarketEntities.XVSM.XvsmISRContainer;
import Model.IssueStockRequest;
import Model.Stock;
import Service.ConnectionError;
import Service.ICompany;
import Util.XvsmUtil;
import org.mozartspaces.core.*;

import java.util.List;

/**
 * Created by Felix on 06.04.2015.
 */
public class XvsmCompany extends XvsmService implements ICompany {

    public XvsmCompany(String uri) throws ConnectionError {
        super(uri);
    }

    @Override
    public void issueStocks(IssueStockRequest isr) throws ConnectionError {

        //create TransactionId
        String transactionId = null;

        try {

            //Init transaction
            transactionId = XvsmUtil.createTransaction();

            DepotCompany depotCompany = new XvsmDepotCompany(isr.getCompany(),transactionId);

            //Write to company-depot
            System.out.print("Writing new stocks to depot ... ");
            depotCompany.addStocks(isr.toStocks(),transactionId);
            System.out.println("done.");

            //Issue Stocks
            System.out.print("Writing IS-request to container ... ");
            ISRContainer isrContainer = new XvsmISRContainer();
            isrContainer.addIssueStocksRequest(isr, transactionId);
            System.out.println("done.");

            XvsmUtil.commitTransaction(transactionId);
            System.out.println("Commited: " + isr);

        } catch (MzsCoreException e) {
            try {
                XvsmUtil.rollbackTransaction(transactionId);
                throw new ConnectionError(e);
            } catch (MzsCoreException ex) {
                throw new ConnectionError(ex);
            }
        }
    }
}
