package RMIServer.EntityProviders;

import Model.Stock;
import Model.TradeObject;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public interface IDepotProvider extends IProvider {

    String getDepotName() throws RemoteException;

    int getTotalAmountOfTradeObjects(String transactionId) throws RemoteException;

    void addTradeObjects(List<TradeObject> tradeObjects, String transactionId) throws RemoteException;
}
