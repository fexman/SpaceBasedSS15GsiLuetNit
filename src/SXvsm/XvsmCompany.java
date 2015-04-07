package SXvsm;

import Model.IssueStockRequest;
import SInterface.ConnectionError;
import SInterface.ICompany;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

import java.util.ArrayList;

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
            tx = xc.getCapi().createTransaction(XvsmUtil.TRANSACTION_TIMEOUT, xc.getSpace());
            ContainerReference depotContainer = XvsmUtil.getContainer(XvsmUtil.Container.COMPANY_DEPOT);

            //Take company-depot
            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(KeyCoordinator.newSelector(isr.getCompanyId()));
            int entriesYet = 0;
            try {
                entriesYet = xc.getCapi().test(depotContainer, selectors, MzsConstants.RequestTimeout.ZERO, tx);
            } catch (CountNotMetException e) {
                System.out.println("Depot-Container seems to be completely emtpy.");
            }
            int newAmount = 0;
            if (entriesYet > 1) {
                throw new ConnectionError(new Exception("FATAL ERROR: MULTIPLE COMPANY-DEPOT ENTRIES"));
            } else if ( entriesYet == 0) {
                newAmount = isr.getAmount();
            } else { //DANGEROUS TYPECAST BUT SHOULD BE OKAY
                int oldAmount = (Integer)xc.getCapi().take(depotContainer,selectors,MzsConstants.RequestTimeout.ZERO,tx).get(0);
                newAmount = oldAmount + isr.getAmount();
            }

            //Write to company-depot
            System.out.print("Writing new stock-amount to depot ... ");
            CoordinationData coordData = KeyCoordinator.newCoordinationData(isr.getCompanyId());
            xc.getCapi().write(depotContainer, XvsmUtil.TRANSACTION_TIMEOUT, tx, new Entry(newAmount, coordData));
            System.out.println("done.");

            //Get issue-stock-request ontainer
            ContainerReference isrContainer = XvsmUtil.getContainer(XvsmUtil.Container.ISSUED_STOCK_REQUESTS);
            System.out.print("Writing IS-request to container ... ");
            xc.getCapi().write(isrContainer, XvsmUtil.TRANSACTION_TIMEOUT, tx, new Entry(isr));
            System.out.println("done.");

            System.out.print("Commiting changes ... ");
            xc.getCapi().commitTransaction(tx);
            System.out.println("done. Commit: " + isr + " | Stockdepot: " + newAmount);

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
