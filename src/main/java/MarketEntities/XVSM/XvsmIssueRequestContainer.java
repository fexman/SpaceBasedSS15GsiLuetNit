package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.IssueRequestContainer;
import Model.IssueRequest;
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
public class XvsmIssueRequestContainer extends IssueRequestContainer {

    private ContainerReference irContainer;
    private XvsmUtil.XvsmConnection xc;
    private XvsmUtil util;

    public XvsmIssueRequestContainer(XvsmUtil util) {
        this.util = util;
        irContainer = util.getContainer(Container.ISSUED_REQUESTS);
        xc = util.getXvsmConnection();
    }

    @Override
    public void addIssueRequest(IssueRequest ir, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        try {
            xc.getCapi().write(irContainer, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(ir));
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<IssueRequest> takeIssueRequests(String transactionId) throws ConnectionErrorException, TransactionTimeoutException {
        TransactionReference tx = util.getTransaction(transactionId);

        ArrayList<Selector> selectors = new ArrayList<>();
        selectors.add(FifoCoordinator.newSelector());

        try {
            return xc.getCapi().take(irContainer, selectors, XvsmUtil.INFINITE_TAKE, tx);
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
            notificationManager.createNotification(irContainer, (NotificationListener)subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionErrorException(e);
        }
    }
}
