package MarketEntities.XVSM;

import MarketEntities.Subscribing.ASubManager;
import Service.ConnectionError;

/**
 * Created by Felix on 23.04.2015.
 */
public interface ISubscribeable  {

    void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError;
}
