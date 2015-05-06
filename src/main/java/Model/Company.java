package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 09.04.2015.
 */
public class Company extends StockOwner {

    public Company(String companyId) {
        this.id = companyId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        return !(id != null ? !id.equals(company.id) : company.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
