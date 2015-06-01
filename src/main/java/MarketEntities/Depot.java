package MarketEntities;

import Model.Stock;
import Model.TradeObject;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class Depot {

    //IMPORTANT, THIS HAS TO BE SET IN THE CONCRETE SUBCLASS
    protected String depotName;

    public String getDepotName() {
        return depotName;
    }

    public abstract int getTotalAmountOfTradeObjects(String transactionId) throws ConnectionErrorException;

    public abstract void addTradeObjects(List<TradeObject> tradeObjects, String transactionId) throws ConnectionErrorException;

}
