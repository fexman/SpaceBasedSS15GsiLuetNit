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

    public IssueStockRequest(String companyId, Integer amount, Double price) {
        this.companyId = companyId;
        this.price = price;
        this.amount = amount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public Integer getAmount() {
        return amount;
    }

    public Double getPrice() {
        return price;
    }

    public List<Stock> toStocks() {
        List<Stock> stocks = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            stocks.add(new Stock(getCompanyId()));
        }
        return stocks;
    }

    public String toString() {
        return companyId+"-Stocks: #"+amount+" for "+price+",-";
    }

}
