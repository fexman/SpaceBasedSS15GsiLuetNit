package MarketEntities.RMI;

import MarketEntities.BrokerSupportContainer;
import Model.MarketValue;
import Model.TradeOrder;
import RMIServer.CallbackDummy;
import RMIServer.EntityProviders.IBrokerSupportProvider;
import RMIServer.ICallbackDummy;
import Service.ConnectionError;
import Util.Container;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by j0h1 on 06.05.2015.
 */
public class RmiBrokerSupportContainer extends BrokerSupportContainer {

    private IBrokerSupportProvider bspContainer;

    public RmiBrokerSupportContainer() {
        bspContainer = (IBrokerSupportProvider) RmiUtil.getContainer(Container.BROKER_TOSUPPORT);
    }

    @Override
    public List<TradeOrder> takeNewTradeOrders(String transactionId) throws ConnectionError {
        try {
            ICallbackDummy callerDummy = new CallbackDummy();
            UnicastRemoteObject.exportObject(callerDummy, 0);
            return bspContainer.takeNewTradeOrders(transactionId,callerDummy);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<MarketValue> takeNewStockPrices(String transactionId) throws ConnectionError {
        try {
            ICallbackDummy callerDummy = new CallbackDummy();
            UnicastRemoteObject.exportObject(callerDummy,0);
            return bspContainer.takeNewStockPrices(transactionId,callerDummy);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

}
