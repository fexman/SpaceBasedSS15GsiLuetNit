package MarketEntities.XVSM;

import MarketEntities.TransactionHistoryContainer;
import Model.HistoryEntry;
import Service.ConnectionError;
import Util.Container;
import Util.XvsmUtil;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;

import java.util.ArrayList;
import java.util.List;

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

}
