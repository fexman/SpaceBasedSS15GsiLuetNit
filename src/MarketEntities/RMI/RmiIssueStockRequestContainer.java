package MarketEntities.RMI;

import MarketEntities.IssueStockRequestContainer;
import Model.IssueStockRequest;
import RMIServer.EntityHandler.IIssueStockRequestContainerHandler;
import Service.ConnectionError;
import Service.Subscribing.IssueStockRequests.AIssueStockRequestSubManager;
import Util.RmiUtil;
import Util.XvsmUtil;
import org.mozartspaces.core.ContainerReference;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiIssueStockRequestContainer extends IssueStockRequestContainer {

    private IIssueStockRequestContainerHandler isrHandler;

    public RmiIssueStockRequestContainer() {
        isrHandler = (IIssueStockRequestContainerHandler)RmiUtil.getHandler(XvsmUtil.Container.ISSUED_STOCK_REQUESTS);
    }

    @Override
    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionError {
        try {
            isrHandler.addIssueStocksRequest(isr,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionError {
        try {
            return isrHandler.takeIssueStockRequests(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void subscribe(AIssueStockRequestSubManager subscriber, String transactionId) throws ConnectionError {

    }
}
