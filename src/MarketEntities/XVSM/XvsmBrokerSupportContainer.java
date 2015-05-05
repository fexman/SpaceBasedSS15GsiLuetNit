package MarketEntities.XVSM;

import MarketEntities.BrokerSupportContainer;
import Model.MarketValue;
import Model.TradeOrder;
import Service.ConnectionError;
import Util.Container;
import Util.XvsmUtil;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;

import java.util.List;

/**
 * Created by Felix on 04.05.2015.
 */
public class XvsmBrokerSupportContainer extends BrokerSupportContainer {

    private ContainerReference spSupportContainer;
    private ContainerReference toSupportContainer;
    private XvsmUtil.XvsmConnection xc;

    public XvsmBrokerSupportContainer() {
        spSupportContainer = XvsmUtil.getContainer(Container.BROKER_SPSUPPORT);
        toSupportContainer = XvsmUtil.getContainer(Container.BROKER_TOSUPPORT);
        xc = XvsmUtil.getXvsmConnection();
    }

    @Override
    public List<TradeOrder> takeNewTradeOrders(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        Selector selector = FifoCoordinator.newSelector();

        try {
            return xc.getCapi().take(toSupportContainer, selector, MzsConstants.RequestTimeout.INFINITE, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<MarketValue> takeNewStockPrices(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        Selector selector = FifoCoordinator.newSelector();

        try {
            return xc.getCapi().take(spSupportContainer, selector, MzsConstants.RequestTimeout.INFINITE, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }
}
