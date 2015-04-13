package SXvsm;

import Model.IssueStockRequest;
import Model.Stock;
import SInterface.ConnectionError;
import SInterface.ICompany;
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
        TransactionReference tx = null;
        try {
            //Get company-depot container
            tx = xc.getCapi().createTransaction(XvsmUtil.ACTION_TIMEOUT, xc.getSpace());
            ContainerReference depotContainer = XvsmUtil.getDepot(isr.getCompany());

            //Write to company-depot
            System.out.print("Writing new stocks to depot ... ");
            List<Stock> stocks = isr.toStocks();
            for (Stock s : stocks) {
                xc.getCapi().write(depotContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(s));
            }

            //Get issue-stock-request container
            ContainerReference isrContainer = XvsmUtil.getContainer(XvsmUtil.Container.ISSUED_STOCK_REQUESTS);
            System.out.print("Writing IS-request to container ... ");
            xc.getCapi().write(isrContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(isr));

            System.out.print("Commiting changes ... ");
            xc.getCapi().commitTransaction(tx);
            System.out.println("done. Commit: " + isr + " | Stockdepot: " + stocks.size());

        } catch (MzsCoreException e) {
            try {
                e.printStackTrace();
                xc.getCapi().rollbackTransaction(tx);
                throw new ConnectionError(e);
            } catch (MzsCoreException ex) {
                throw new ConnectionError(ex);
            }
        }
    }
}
