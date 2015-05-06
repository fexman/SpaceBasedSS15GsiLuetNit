package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.ISRContainer;
import Model.IssueStockRequest;
import Service.ConnectionErrorException;
import Service.TransactionTimeoutException;
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
    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        try {
            xc.getCapi().write(isrContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(isr));
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionErrorException, TransactionTimeoutException {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        ArrayList<Selector> selectors = new ArrayList<>();
        selectors.add(FifoCoordinator.newSelector());

        try {
            return xc.getCapi().take(isrContainer, selectors, XvsmUtil.INFINITE_TAKE, tx);
        } catch (MzsTimeoutException e) {
            throw new TransactionTimeoutException(e);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
        NotificationManager notificationManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<>();
        operations.add(Operation.WRITE);

        try {
            notificationManager.createNotification(isrContainer, (NotificationListener)subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionErrorException(e);
        }
    }
}
