package MarketEntities.XVSM;

import MarketEntities.TradeOrdersContainer;
import Model.TradeOrder;
import Util.XvsmUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.TransactionReference;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmTradeOrdersContainer extends TradeOrdersContainer {

    private ContainerReference tradeOrdersContainer;
    private XvsmUtil.XvsmConnection xc;

    public XvsmTradeOrdersContainer() {
        tradeOrdersContainer = XvsmUtil.getContainer(XvsmUtil.Container.TRADE_ORDERS);
        xc = XvsmUtil.getXvsmConnection();
    }


    @Override
    public void addOrUpdateOrder(TradeOrder order, String transactionId) {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

    }

    @Override
    public List<TradeOrder> getOrders(TradeOrder order, String transactionId) {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
        return null;
    }

    @Override
    public List<TradeOrder> getAllorders(String transactionId) {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
        return null;
    }
}
