package MarketEntities.XVSM;

import MarketEntities.DepotInvestor;
import MarketEntities.Subscribing.ASubManager;
import Model.Company;
import Model.Investor;
import Model.Stock;
import Model.TradeObject;
import Service.ConnectionErrorException;
import Util.*;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by j0h1 on 22.04.2015.
 */
public class XvsmDepotInvestor extends DepotInvestor {

    private ContainerReference investorDepot;
    private XvsmUtil.XvsmConnection xc;
    private XvsmUtil util;

    public XvsmDepotInvestor(XvsmUtil util, Investor investor, String transactionId) throws ConnectionErrorException {
        super(investor, transactionId);

        this.depotName = Util.Container.DEPOT_INVESTOR_TOKEN.toString() + investor.getId();

        this.util = util;
        xc = util.getXvsmConnection();
        try {
            TransactionReference tx = util.getTransaction(transactionId);
            investorDepot = util.getDepot(investor.getId(), tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public double getBudget(String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

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
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void setBudget(double amount, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector("BUDGET", MzsConstants.Selecting.COUNT_MAX);
        try {
            xc.getCapi().delete(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
            xc.getCapi().write(new Entry(amount, LabelCoordinator.newCoordinationData("BUDGET")), investorDepot, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<TradeObject> takeTradeObjects(String toId, int amount, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector(toId, amount);
        try {
            List<TradeObject> results = xc.getCapi().take(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
            return results;
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getTradeObjectAmount(String toId, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        LabelCoordinator.LabelSelector selector = LabelCoordinator.newSelector(toId, MzsConstants.Selecting.COUNT_MAX);
        try {
            return xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx).size();
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<TradeObject> readAllTradeObjects(String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        TypeCoordinator.TypeSelector selector = TypeCoordinator.newSelector(TradeObject.class, MzsConstants.Selecting.COUNT_MAX);
        try {
            return xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public int getTotalAmountOfTradeObjects(String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        TypeCoordinator.TypeSelector selector = TypeCoordinator.newSelector(Stock.class, MzsConstants.Selecting.COUNT_MAX);
        try {
            return xc.getCapi().read(investorDepot, selector, XvsmUtil.ACTION_TIMEOUT, tx).size();
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void addTradeObjects(List<TradeObject> tradeObjects, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        for (TradeObject tradeObject : tradeObjects) {
            try {
                xc.getCapi().write(investorDepot, XvsmUtil.ACTION_TIMEOUT, tx, new Entry(tradeObject, LabelCoordinator.newCoordinationData(tradeObject.getId())));
            } catch (MzsCoreException e) {
                throw new ConnectionErrorException(e);
            }
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
        NotificationManager notificationManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<>();
        operations.add(Operation.WRITE);

        try {
            notificationManager.createNotification(investorDepot, (NotificationListener) subscriber, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionErrorException(e);
        }
    }
}
