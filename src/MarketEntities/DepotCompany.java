package MarketEntities;

import Model.Company;
import Model.Stock;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class DepotCompany extends Depot {

    private Company comp;

    public DepotCompany(Company comp, String transactionId) throws ConnectionErrorException {
        this.comp = comp;
    }

    public Company getCompany() {
        return comp;
    }

    public abstract List<Stock> takeStocks(int amount, String transactionId) throws ConnectionErrorException;

}
