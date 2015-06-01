package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 06.04.2015.
 */
public class IssueStockRequest implements Serializable {


    private static final long serialVersionUID = 545149035659778572L;

    private String companyId;
    private Double price;
    private Integer amount;

    public IssueStockRequest(Company company, Integer amount, Double price) {
        this.companyId = company.getId();
        this.price = price;
        this.amount = amount;
    }

    public Company getCompany() {
        return new Company(companyId);
    }

    public Integer getAmount() {
        return amount;
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
            stocks.add(new Stock(new Company(companyId)));
        }
        return stocks;
    }

    public List<TradeObject> toTradeObjects() {
        List<TradeObject> tradeObjects = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            tradeObjects.add(new Stock(new Company(companyId)));
        }
        return tradeObjects;
    }

    public String toString() {
        return companyId+"-Stocks: #"+amount+" for "+price+",-";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssueStockRequest that = (IssueStockRequest) o;

        if (companyId != null ? !companyId.equals(that.companyId) : that.companyId != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        return !(amount != null ? !amount.equals(that.amount) : that.amount != null);

    }

    @Override
    public int hashCode() {
        int result = companyId != null ? companyId.hashCode() : 0;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
