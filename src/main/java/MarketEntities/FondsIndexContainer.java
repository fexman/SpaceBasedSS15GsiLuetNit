package MarketEntities;

import Model.AddressInfo;
import Model.Investor;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 05.06.2015.
 */
public abstract class FondsIndexContainer {

    public abstract List<AddressInfo> getMarkets(Investor investor, String transactionId) throws ConnectionErrorException;

    public abstract void registerMarkets(Investor investor, List<AddressInfo> markets, String transactionId) throws ConnectionErrorException;
}
