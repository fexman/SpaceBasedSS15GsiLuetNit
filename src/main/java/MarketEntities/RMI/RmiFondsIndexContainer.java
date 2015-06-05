package MarketEntities.RMI;

import MarketEntities.FondsIndexContainer;
import Model.AddressInfo;
import Model.Investor;
import RMIServer.EntityProviders.IFondsIndexProvider;
import Service.ConnectionErrorException;
import Util.Container;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 05.06.2015.
 */
public class RmiFondsIndexContainer extends FondsIndexContainer {

    private IFondsIndexProvider fiContainer;
    private RmiUtil util;

    public RmiFondsIndexContainer(RmiUtil util) {
        this.util = util;
        fiContainer = (IFondsIndexProvider) util.getContainer(Container.FONDS_INDEX_CONTAINER);
    }

    @Override
    public List<AddressInfo> getMarkets(Investor investor, String transactionId) throws ConnectionErrorException {
        try {
            return fiContainer.getMarkets(investor,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void registerMarkets(Investor investor, List<AddressInfo> markets, String transactionId) throws ConnectionErrorException {
        try {
            fiContainer.registerMarkets(investor, markets, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }
}
