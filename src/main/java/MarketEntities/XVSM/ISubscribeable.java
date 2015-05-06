package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import Service.ConnectionErrorException;

/**
 * Created by Felix on 23.04.2015.
 */
public interface ISubscribeable  {

    void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException;
}
