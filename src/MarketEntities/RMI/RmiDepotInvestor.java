package MarketEntities.RMI;

import MarketEntities.DepotInvestor;
import MarketEntities.Subscribing.ASubManager;
import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.Investor;
import Model.Stock;
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

    public RmiDepotInvestor(Investor investor, String transactionId) throws ConnectionErrorException {
        super(investor, transactionId);

        this.depotName = Container.DEPOT_INVESTOR_TOKEN + investor.getId();

        depotInvestor = RmiUtil.getDepot(investor.getId());
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
    public List<Stock> takeStocks(Company comp, int amount, String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.takeStocks(comp, amount, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getStockAmount(String stockName, String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.getStockAmount(stockName, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<Stock> readAllStocks(String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.readAllStocks(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws ConnectionErrorException {
        try {
            return depotInvestor.getTotalAmountOfStocks(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void addStocks(List<Stock> stocks, String transactionId) throws ConnectionErrorException {
        try {
            depotInvestor.addStocks(stocks, transactionId);
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
