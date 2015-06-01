package RMIServer.EntityProviders.Impl;

import Model.IssueStockRequest;
import Model.TradeOrder;
import RMIServer.EntityProviders.IBrokerSupportProvider;
import RMIServer.EntityProviders.ITradeOrderProvider;
import MarketEntities.Subscribing.IRmiCallback;
import RMIServer.ICallbackDummy;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 22.04.2015.
 */
public class TradeOrderProvider implements ITradeOrderProvider {

    private Set<TradeOrder> tradeOrders;
    private Object lock;
    private Set<IRmiCallback<TradeOrder>> callbacks;
    private IBrokerSupportProvider bsp;

    public TradeOrderProvider(IBrokerSupportProvider bsp) {
        tradeOrders = new HashSet<>();
        callbacks = new HashSet<>();
        lock = new Object();
        this.bsp = bsp;
    }


    @Override
    public void addOrUpdateOrder(TradeOrder order, String transactionId) throws RemoteException {
        synchronized (lock) {
            // updating order (Sets use equals() on the objects - if only add() is used, equal objects (objects with same id) won't be updated
            // so if a order is updated -> remove and add it instead of just adding (e.g. for TO deletion)
            if (tradeOrders.contains(order)) {
                tradeOrders.remove(order);
            }
            tradeOrders.add(order);
            synchronized (order) {
                if (order.getJustChanged()) {
                    order.notifyAll(); //Wake up one Thread waiting for resources
                }
            }
        }

        List<TradeOrder> newTOs = new ArrayList<TradeOrder>();
        newTOs.add(order);
        if (order.getJustChanged()) {
            bsp.addNewTradeOrders(newTOs);
            System.out.println(getClass().getSimpleName() + ": addOrUpdateOrder :" + order);
        }
        for (IRmiCallback<TradeOrder> callback : callbacks) {
            callback.newData(newTOs);
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
                if (order.getJustChanged() != null) {
                    if (!order.getJustChanged().equals(to.getJustChanged())) {
                        continue toLoop;
                    }
                }
                if (order.getPriceLimit() != null) {
                    switch (order.getType()) {
                        case BUY_ORDER: //LOOKING FOR BUY ORDER, INFINITE AM TRYING TO SELL SOMETHING -> PRICE SHOULD BY ABOVE (OR EQUAL TO) MY LIMIT
                            if (to.getPriceLimit() < order.getPriceLimit()) {
                                continue toLoop;
                            }
                            break;
                        case SELL_ORDER: //LOOKING FOR SELL ORDER, INFINITE AM TRYING TO BUY SOMETHING -> PRICE SHOULD BY UNDER (OR EQUAL TO) MY LIMIT
                            if (to.getPriceLimit() > order.getPriceLimit()) {
                                continue toLoop;
                            }
                            break;
                        case ANY: //INFINITE DONT CARE, SIMPLE MATCHING

                    }
                }

                if (order.getType() != null) {
                    if (!order.getType().equals(TradeOrder.Type.ANY)) {
                        if (!to.getType().equals(order.getType())) {
                            continue toLoop;
                        }
                    }
                }

                if (order.isPrioritized() != null) {
                    if (to.isPrioritized() != order.isPrioritized()) {
                        continue toLoop;
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
                    case ANY: // INFINITE DONT CARE, GIVE ME ALL OF THEM
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
    public TradeOrder takeOrder(TradeOrder tradeOrder, String transactionId) throws RemoteException {

        synchronized (lock) { // lock provider


            System.out.println(this.getClass().getSimpleName()+" IS HERE BIATCH!! DOING SOME TRADE ORDER TAKING.");
            System.out.println("IM LOOKING FOR: "+tradeOrder);
            for (TradeOrder to : tradeOrders) {
                System.out.println("IVE GOT THIS: "+to);
            }

            List<TradeOrder> results = getOrders(tradeOrder, transactionId);
            if (results.size() > 0) {
                System.out.println("SIZE WAS OK");
                tradeOrders.remove(results.get(0));
                System.out.println("GONNA RETURN THIS: "+results.get(0));
                return results.get(0);
            }
            System.out.println("SIZE WAS 0 THOUGH");
            return null;
        }

    }

    @Override
    public void subscribe(IRmiCallback<TradeOrder> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(IRmiCallback<TradeOrder> callback) throws RemoteException {
        callbacks.remove(callback);
    }

    public String toString() {
        String info = "";
        info += "===== TRADEORDERS CONTAINER ====\n";
        info += "callbacks: "+callbacks.size()+"\n";
        info += "entries: "+tradeOrders.size()+"\n";
        info += "================================\n";
        int counter = 1;
        if (!tradeOrders.isEmpty()) {
            for (TradeOrder to : tradeOrders) {
                info += "[" + counter + "]: " + to.toString() + "\n";
                counter++;
            }
            info += "================================\n";
        }
        return info;
    }

}
