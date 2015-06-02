package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 06.04.2015.
 */
public class IssueStockRequest extends IssueRequest {


    private static final long serialVersionUID = 545149035659778572L;

    private Double price;

    public IssueStockRequest(Company company, Integer amount, Double price) {
        this.id = company.getId();
        this.price = price;
        this.amount = amount;
    }

    public Company getCompany() {
        return new Company(id);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Stock> toStocks() {
        List<Stock> stocks = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            stocks.add(new Stock(new Company(id)));
        }
        return stocks;
    }

    public List<TradeObject> toTradeObjects() {
        List<TradeObject> tradeObjects = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            tradeObjects.add(new Stock(new Company(id)));
        }
        return tradeObjects;
    }

    public String toString() {
        return id+"-Stocks: #"+amount+" for "+price+",-";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssueStockRequest that = (IssueStockRequest) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        return !(amount != null ? !amount.equals(that.amount) : that.amount != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
