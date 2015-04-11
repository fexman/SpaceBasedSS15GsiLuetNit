package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 09.04.2015.
 */
public class Company implements Serializable {

    private static final long serialVersionUID = 3116234320068709532L;
    private String id;

    public Company(String companyId) {
        this.id = companyId;
    }

    public String getId() {
        return id;
    }

    public List<Stock> createStocks(int amount) {
        List<Stock> stocks = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            stocks.add(new Stock(this));
        }
        return stocks;
    }

    public IssueStockRequest createIssueStockRequest(int amount, double price) {
        return new IssueStockRequest(this,amount,price);
    }
}
