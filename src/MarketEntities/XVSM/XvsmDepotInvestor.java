package MarketEntities.XVSM;

import MarketEntities.DepotInvestor;
import MarketEntities.StockPricesContainer;
import Model.Company;
import Model.Investor;
import Model.Stock;
import Service.ConnectionError;
import Util.*;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.xvsmp.util.PredefinedCoordinationDataCreators;

import java.io.Serializable;
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
            investorDepot = XvsmUtil.getDepot(investor.getId(), tx);
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
                Serializable obj = budget.get(0);
                return ((Double) obj).doubleValue();
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

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector(stockName, MzsConstants.Selecting.COUNT_MAX);
        try {
            return xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx).size();
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<Stock> readAllStocks(String transactionId) throws ConnectionError {
//        TransactionReference tx = XvsmUtil.getTransaction(transactionId);
//
//
//        TypeCoordinator.TypeSelector selector = TypeCoordinator.newSelector(Stock.class);
//        try {
//            return xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
//        } catch (MzsCoreException e) {
//            throw new ConnectionError(e);
//        }
        //TODO find a way to read only STOCKS from container

        return new ArrayList<>();
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
                xc.getCapi().write(investorDepot, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(stock, LabelCoordinator.newCoordinationData(stock.getCompany().getId())));
            } catch (MzsCoreException e) {
                throw new ConnectionError(e);
            }
        }
    }
}
