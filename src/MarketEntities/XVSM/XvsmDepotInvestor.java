package MarketEntities.XVSM;

import MarketEntities.DepotInvestor;
import Model.Company;
import Model.Investor;
import Model.Stock;
import Service.ConnectionError;
import Util.*;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j0h1 on 22.04.2015.
 */
public class XvsmDepotInvestor extends DepotInvestor {

    private ContainerReference investorDepot;
    private XvsmUtil.XvsmConnection xc;

    public XvsmDepotInvestor(Investor investor, String transactionId) throws ConnectionError {
        super(investor, transactionId);

        this.depotName = Util.Container.DEPOT_INVESTOR_TOKEN.toString() + investor.getId();

        xc = XvsmUtil.getXvsmConnection();
        try {
            TransactionReference tx = XvsmUtil.getTransaction(transactionId);
            investorDepot = XvsmUtil.getDepot(investor, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public double getBudget(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector("BUDGET", MzsConstants.Selecting.COUNT_MAX);
        try {
            ArrayList<Entry> budget = xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
            if (budget.size() > 0) {
                return (double) budget.get(0).getValue();
            } else {
                xc.getCapi().write(new Entry(0.00, LabelCoordinator.newCoordinationData("BUDGET")), investorDepot, XvsmUtil.ACTION_TIMEOUT, tx);
                return 0.00;
            }
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void setBudget(double amount, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector("BUDGET", MzsConstants.Selecting.COUNT_MAX);
        try {
            xc.getCapi().delete(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
            xc.getCapi().write(new Entry(amount, LabelCoordinator.newCoordinationData("BUDGET")), investorDepot, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<Stock> takeStocks(Company comp, int amount, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector(comp.getId(), amount);
        try {
            return xc.getCapi().take(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public int getStockAmount(String stockName, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector(stockName);
        try {
            return xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx).size();
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector(null, MzsConstants.Selecting.COUNT_MAX);
        try {
            return xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx).size() - 1;
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void addStocks(List<Stock> stocks, String transactionId) throws ConnectionError {
        TransactionReference tx = XvsmUtil.getTransaction(transactionId);

        for (Stock stock : stocks) {
            try {
                xc.getCapi().write(investorDepot, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(stock));
            } catch (MzsCoreException e) {
                throw new ConnectionError(e);
            }
        }
    }
}
