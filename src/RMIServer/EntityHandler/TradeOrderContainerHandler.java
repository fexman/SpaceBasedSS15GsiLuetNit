package RMIServer.EntityHandler;

import Model.IssueStockRequest;
import Model.TradeOrder;
import RMIServer.RmiCallback;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 22.04.2015.
 */
public class TradeOrderContainerHandler implements ITradeOrderContainerHandler  {

    private Set<TradeOrder> tradeOrders;
    private Object lock;
    private Set<RmiCallback<TradeOrder>> callbacks;

    public TradeOrderContainerHandler() {
        tradeOrders = new HashSet<>();
        callbacks = new HashSet<>();
        lock = new Object();
    }


    @Override
    public void addOrUpdateOrder(TradeOrder order, String transactionId) throws RemoteException {
        synchronized (lock) {
            tradeOrders.add(order);
        }
    }

    @Override
    public List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws RemoteException {
        ArrayList<TradeOrder> matches = new ArrayList<>();
        synchronized (lock) {
            toLoop: for (TradeOrder to : tradeOrders) {
                if (order.getId() != null) { //LOOKING FOR SPECIFIC ORDER WITH ID
                    if (!order.getId().equals(to.getId())) {
                        continue toLoop;
                    }
                }
                if (order.getInvestorId() != null) { //LOOKING FOR INVESTORT/TRADER WITH ID XYZ (COMPANY OR INVESTOR)
                    if (!order.getInvestorId().equals(to.getInvestorId())) {
                        continue toLoop;
                    }
                }
                if (order.getCompanyId() != null) { //LOOKING FOR STOCKS OF COMPANY XYZ
                    if (!order.getCompanyId().equals(to.getCompanyId())) {
                        continue toLoop;
                    }
                }
                if (order.getPriceLimit() != null) {
                    switch (order.getType()) {
                        case BUY_ORDER: //LOOKING FOR BUY ORDER, I AM TRYING TO SELL SOMETHING -> PRICE SHOULD BY ABOVE (OR EQUAL TO) MY LIMIT
                            if (to.getPriceLimit() < order.getPriceLimit()) {
                                continue toLoop;
                            }
                            break;
                        case SELL_ORDER: //LOOKING FOR SELL ORDER, I AM TRYING TO BUY SOMETHING -> PRICE SHOULD BY UNDER (OR EQUAL TO) MY LIMIT
                            if (to.getPriceLimit() > order.getPriceLimit()) {
                                continue toLoop;
                            }
                            break;
                        case ANY: //I DONT CARE, SIMPLE MATCHING

                    }
                }
                switch (order.getStatus()) { //LOOKING FOR ORDERS WITH STATUS ...
                    case OPEN: // OPEN
                        if (to.getStatus() != TradeOrder.Status.OPEN) {
                            continue toLoop;
                        }
                        break;
                    case PARTIALLY_COMPLETED: // PARTIALLY COMPLETED
                        if (to.getStatus() != TradeOrder.Status.PARTIALLY_COMPLETED) {
                            continue toLoop;
                        }
                        break;
                    case NOT_COMPLETED: //OPEN OR PARTIALLY COMPLETED
                        if (!(to.getStatus() == TradeOrder.Status.OPEN || to.getStatus() == TradeOrder.Status.PARTIALLY_COMPLETED)) {
                            continue toLoop;
                        }
                        break;
                    case COMPLETED: // COMPLETED
                        if (to.getStatus() != TradeOrder.Status.COMPLETED) {
                            continue toLoop;
                        }
                        break;
                    case DELETED: // DELETED
                        if (to.getStatus() != TradeOrder.Status.DELETED) {
                            continue toLoop;
                        }
                        break;
                    case NOT_DELETED: //EVERYTHING EXCEPT DELETED
                        if (to.getStatus() == TradeOrder.Status.DELETED) {
                            continue toLoop;
                        }
                        break;
                    case ANY: // I DONT CARE, GIVE ME ALL OF THEM
                        break;
                }

                //Passed all criterias! ADD TO RESULT
                matches.add(to);
            }
        }
        return matches;
    }

    @Override
    public List<TradeOrder> getAllOrders(String transactionId) throws RemoteException {
        return new ArrayList<>(tradeOrders);
    }

    @Override
    public void subscribe(RmiCallback<TradeOrder> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(RmiCallback<TradeOrder> callback) throws RemoteException {
        callbacks.remove(callback);
    }
}
