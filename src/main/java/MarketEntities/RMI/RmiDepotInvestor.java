package MarketEntities.RMI;

import MarketEntities.DepotInvestor;
import MarketEntities.Subscribing.ASubManager;
import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.Investor;
import Model.TradeObject;
import RMIServer.EntityProviders.IDepotInvestorProvider;
import Service.ConnectionErrorException;
import Util.Container;
import Util.RmiUtil;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by j0h1 on 01.05.2015.
 */
public class RmiDepotInvestor extends DepotInvestor {

    private IDepotInvestorProvider depotInvestor;
    private RmiUtil util;

    public RmiDepotInvestor(RmiUtil util, Investor investor, String transactionId) throws ConnectionErrorException {
        super(investor, transactionId);

        this.util = util;
        this.depotName = Container.DEPOT_INVESTOR_TOKEN + investor.getId();

        depotInvestor = util.getDepot(investor.getId());
    }

    @Override
    public double getBudget(String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.getBudget(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void setBudget(double amount, String transactionId) throws ConnectionErrorException {
        try {
            depotInvestor.setBudget(amount, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<TradeObject> takeTradeObjects(String toId, int amount, String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.takeTradeObjects(toId, amount, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getTradeObjectAmount(String toId, String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.getTradeObjectAmount(toId, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<TradeObject> readAllTradeObjects(String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.readAllTradeObjects(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getTotalAmountOfTradeObjects(String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.getTotalAmountOfTradeObjects(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void addTradeObjects(List<TradeObject> stocks, String transactionId) throws ConnectionErrorException {
        try {
            depotInvestor.addTradeObjects(stocks, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
        IRmiCallback<Serializable> rmiSub = (IRmiCallback<Serializable>) subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub, 0);
            depotInvestor.subscribe(rmiSub);
        } catch (RemoteException e) {
            if (e.getMessage().contains("already exported")) { //Export only once
                try {
                    depotInvestor.subscribe(rmiSub);
                    return;
                } catch (RemoteException e1) {
                    throw new ConnectionErrorException(e);
                }
            }
            throw new ConnectionErrorException(e);
        }
    }
}
