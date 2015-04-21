package RMIServer;


import RMIServer.EntityHandler.IIssueStockRequestContainerHandler;

import java.rmi.Remote;

/**
 * Created by Felix on 21.04.2015.
 */
public interface IRmiServer extends Remote {

    IIssueStockRequestContainerHandler getIssueStockRequestContainer();
}
