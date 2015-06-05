package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.AddressInfo;
import Model.Investor;
import Model.IssueRequest;
import RMIServer.EntityProviders.IFondsIndexProvider;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by Felix on 05.06.2015.
 */
public class FondsIndexProvider implements IFondsIndexProvider {

    private volatile HashMap<Investor, List<AddressInfo>> fondsIndex;
    private Object lock;


    public FondsIndexProvider() {
        fondsIndex = new HashMap<>();
        lock = new Object();
    }

    @Override
    public List<AddressInfo> getMarkets(Investor investor, String transactionId) throws RemoteException {
        synchronized (lock) {
            System.out.println(this.getClass().getSimpleName()+": getMarkets");
            List<AddressInfo> result = fondsIndex.get(investor);
            if (result != null) {
                System.out.println(this.getClass().getSimpleName() + ": returning " + result.size() + " known markets for investor: " + investor.getId());
            }
            return result;
        }
    }

    @Override
    public void registerMarkets(Investor investor, List<AddressInfo> markets, String transactionId) throws RemoteException {
        synchronized (lock) {
            System.out.println(this.getClass().getSimpleName()+": registerMarkets");
            System.out.println(this.getClass().getSimpleName()+": Putting in for investor: " + investor.getId());
            for (AddressInfo address: markets) {
                System.out.println("\t"+address);
            }
            fondsIndex.put(investor,markets);
        }

    }
}
