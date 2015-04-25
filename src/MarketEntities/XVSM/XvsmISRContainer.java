package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.ISRContainer;
import Model.IssueStockRequest;
import Service.ConnectionError;
import Util.Container;
import Util.XvsmUtil;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 14.04.2015.
 */
public class XvsmISRContainer extends ISRContainer {

    private ContainerReference isrContainer;
    private XvsmUtil.XvsmConnection xc;

    public XvsmISRContainer() {
        isrContainer = XvsmUtil.getContainer(Container.ISSUED_STOCK_REQUESTS);
        xc = XvsmUtil.getXvsmConnection();
    }

    @Override
    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        try {
            xc.getCapi().write(isrContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(isr));
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        ArrayList<Selector> selectors = new ArrayList<>();
        selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));

        try {
            return xc.getCapi().take(isrContainer, selectors, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError {
        NotificationManager notificationManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<>();
        operations.add(Operation.WRITE);

        try {
            notificationManager.createNotification(isrContainer, (NotificationListener)subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionError(e);
        }
    }
}
