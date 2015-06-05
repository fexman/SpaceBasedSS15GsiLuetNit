package RMIServer.EntityProviders;

import Model.AddressInfo;
import Model.Investor;
import Service.ConnectionErrorException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 05.06.2015.
 */
public interface IFondsIndexProvider extends IProvider{

    List<AddressInfo> getMarkets(Investor investor, String transactionId) throws RemoteException;

    void registerMarkets(Investor investor, List<AddressInfo> markets, String transactionId) throws RemoteException;
}
