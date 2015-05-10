package RMIServer.EntityProviders.Impl;

import Model.MarketValue;
import Model.TradeOrder;
import RMIServer.EntityProviders.IBrokerSupportProvider;
import RMIServer.ICallbackDummy;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 06.05.2015.
 */
public class BrokerSupportProvider implements IBrokerSupportProvider {

    private volatile List<TradeOrder> orders;
    private volatile List<MarketValue> stockPrices;
    private Object lock;

    public BrokerSupportProvider() {
        this.orders = new ArrayList<>();
        this.stockPrices = new ArrayList<>();
        this.lock = new Object();
    }

    @Override
    public void addNewTradeOrders(List<TradeOrder> orders) throws RemoteException {
        synchronized (lock) {
            this.orders.addAll(orders);
            System.out.println(getClass().getSimpleName()+": addNewTradeOrders");
            synchronized (this.orders) {
                this.orders.notifyAll(); //Wake up one Thread waiting for resources
            }
        }
    }

    @Override
    public void addNewStockPrices(List<MarketValue> stockPrices) throws RemoteException {
        synchronized (lock) {
            this.stockPrices.addAll(stockPrices);
            System.out.println(getClass().getSimpleName()+": addNewStockPrices");
            synchronized (this.stockPrices) {
                this.stockPrices.notifyAll(); //Wake up one Thread waiting for resources
            }
        }
    }

    @Override
    public List<TradeOrder> takeNewTradeOrders(String transactionId, ICallbackDummy caller) throws RemoteException {
        repeat: while (true) {


            while (orders.isEmpty()) {
                try {
                    synchronized (orders) { //Only one at a time
                        orders.wait(); //Wait for change in Resources
                    }
                } catch (InterruptedException e) { }
            }

            synchronized (lock) {
                try {

                    if (orders.isEmpty()) {
                        continue repeat;
                    }

                    caller.testConnection();
                    System.out.println(getClass().getSimpleName() + ": takeNewTradeOrders");
                    List<TradeOrder> returnVal = new ArrayList<>(orders);
                    orders = new ArrayList<>();
                    return returnVal;
                } catch (RemoteException e) {
                    System.out.println("That did not work out. :(");
                    return null;
                }
            }
        }

    }

    @Override
    public List<MarketValue> takeNewStockPrices(String transactionId, ICallbackDummy caller) throws RemoteException {
        repeat: while (true) {

                while (stockPrices.isEmpty()) {
                    try {
                        synchronized (stockPrices) { //Only one at a time
                            stockPrices.wait(); //Wait for change in Resources
                        }
                    } catch (InterruptedException e) {
                    }
                }

            synchronized (lock) {
                try {

                    if (stockPrices.isEmpty()) {
                        continue repeat;
                    }

                    caller.testConnection();
                    System.out.println(getClass().getSimpleName() + ": takeNewStockPrices");
                    List<MarketValue> returnVal = new ArrayList<>(stockPrices);
                    stockPrices = new ArrayList<>();
                    return returnVal;
                } catch (RemoteException e) {
                    System.out.println("That did not work out. :(");
                    return null;
                }
            }
        }
    }
}
