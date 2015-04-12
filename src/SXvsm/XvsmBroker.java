package SXvsm;

import Model.IssueStockRequest;
import SInterface.ConnectionError;
import SInterface.IBroker;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 11.04.2015.
 */
public class XvsmBroker extends XvsmService implements IBroker, NotificationListener {

    private ContainerReference issuedStocksContainer;

    public XvsmBroker(String uri) throws ConnectionError {
        super(uri);
        this.issuedStocksContainer = XvsmUtil.getContainer(XvsmUtil.Container.ISSUED_STOCK_REQUESTS);
    }

    @Override
    public void startBroking() throws ConnectionError {



        // Create notification
        NotificationManager notifManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<Operation>();
        operations.add(Operation.WRITE);
        try {
            notifManager.createNotification(issuedStocksContainer, this, operations, null, null);
        } catch (Exception e) {
                throw new ConnectionError(e);
        }

    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        try {
            takeISRs();
        } catch (ConnectionError e) {
            System.out.println("FATAL: CONNECTION ERROR ON LISTENING.");
        }
    }

    private void takeISRs() throws ConnectionError{

        TransactionReference tx = null;

        try {
            //Get company-depot container
            tx = xc.getCapi().createTransaction(XvsmUtil.ACTION_TIMEOUT, xc.getSpace());

            //Take all existing ISRs
            ArrayList<Selector> selectors = new ArrayList<Selector>();
            selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
            ArrayList<IssueStockRequest> resultEntries = xc.getCapi().take(issuedStocksContainer, selectors, XvsmUtil.ACTION_TIMEOUT, tx);
            // output
            for (IssueStockRequest isr : resultEntries) {

                //TODO: PROCESS ISRs!!!!

                System.out.println(isr.toString());
            }
            xc.getCapi().commitTransaction(tx);

        } catch (MzsCoreException e) {
            try {
                xc.getCapi().rollbackTransaction(tx);
                throw new ConnectionError(e);
            } catch (MzsCoreException ex) {
                throw new ConnectionError(ex);
            }
        }
    }
}
