package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.TransactionHistoryContainer;
import Model.HistoryEntry;
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
 * Created by j0h1 on 27.04.2015.
 */
public class XvsmTransactionHistoryContainer extends TransactionHistoryContainer {

    private ContainerReference transactionHistoryContainer;
    private XvsmUtil.XvsmConnection xc;

    public XvsmTransactionHistoryContainer() {
        transactionHistoryContainer = XvsmUtil.getContainer(Container.TRANSACTION_HISTORY);
        xc = XvsmUtil.getXvsmConnection();
    }

    @Override
    public void addHistoryEntry(HistoryEntry historyEntry, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        try {
            xc.getCapi().write(new Entry(historyEntry), transactionHistoryContainer, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<HistoryEntry> getTransactionHistory(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        FifoCoordinator.FifoSelector selector = FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL);

        try {
            return xc.getCapi().read(transactionHistoryContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
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
            notificationManager.createNotification(transactionHistoryContainer, (NotificationListener) subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionError(e);
        }
    }

}
