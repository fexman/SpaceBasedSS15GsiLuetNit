package SXvsm;

import Model.IssueStockRequest;
import Model.Stock;
import SInterface.ConnectionError;
import SInterface.ICompany;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.util.List;

/**
 * Created by Felix on 06.04.2015.
 */
public class XvsmCompany implements ICompany{

    private XvsmUtil.XvsmConnection xc;

    public XvsmCompany(XvsmUtil.XvsmConnection xc) {
        this.xc = xc;
    }

    @Override
    public void issueStocks(IssueStockRequest isr) throws ConnectionError {
        TransactionReference tx = null;
        try {

            //Get comapny-depot ontainer
            tx = xc.getCapi().createTransaction(XvsmUtil.ACTION_TIMEOUT, xc.getSpace());
            ContainerReference depotContainer = XvsmUtil.getContainer(XvsmUtil.Container.COMPANY_DEPOT);

            //Write to company-depot
            System.out.print("Writing new stocks to depot ... ");
            CoordinationData coordData = LabelCoordinator.newCoordinationData(isr.getCompanyId());
            List<Stock> stocks = isr.toStocks();
            for (Stock s : stocks) {
                xc.getCapi().write(depotContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(s, coordData));
            }
            System.out.println("done.");

            //Get issue-stock-request ontainer
            ContainerReference isrContainer = XvsmUtil.getContainer(XvsmUtil.Container.ISSUED_STOCK_REQUESTS);
            System.out.print("Writing IS-request to container ... ");
            xc.getCapi().write(isrContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(isr));
            System.out.println("done.");

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
