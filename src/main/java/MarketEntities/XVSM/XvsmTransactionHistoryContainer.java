package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.TransactionHistoryContainer;
import Model.HistoryEntry;
import Service.ConnectionErrorException;
import Util.Container;
import Util.XvsmUtil;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by j0h1 on 27.04.2015.
 */
public class XvsmTransactionHistoryContainer extends TransactionHistoryContainer {

    private ContainerReference transactionHistoryContainer;
    private XvsmUtil.XvsmConnection xc;
    private XvsmUtil util;

    public XvsmTransactionHistoryContainer(XvsmUtil util) {
        this.util = util;
        transactionHistoryContainer = util.getContainer(Container.TRANSACTION_HISTORY);
        xc = util.getXvsmConnection();
    }

    @Override
    public void addHistoryEntry(HistoryEntry historyEntry, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        try {
            xc.getCapi().write(new Entry(historyEntry), transactionHistoryContainer, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<HistoryEntry> getTransactionHistory(String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        FifoCoordinator.FifoSelector selector = FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL);

        try {
            return xc.getCapi().read(transactionHistoryContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
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
            notificationManager.createNotification(transactionHistoryContainer, (NotificationListener) subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionErrorException(e);
        }
    }

}
