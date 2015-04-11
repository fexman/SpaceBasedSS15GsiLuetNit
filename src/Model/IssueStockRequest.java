package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 06.04.2015.
 */
public class IssueStockRequest implements Serializable {


    private static final long serialVersionUID = 545149035659778572L;
    private Company company;
    private Double price;
    private Integer amount;

    public IssueStockRequest(Company company, Integer amount, Double price) {
        this.company = company;
        this.price = price;
        this.amount = amount;
    }

    public Company getCompany() {
        return company;
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
            stocks.add(new Stock(company.getId()));
        }
        return stocks;
    }

    public String toString() {
        return company.getId()+"-Stocks: #"+amount+" for "+price+",-";
    }

}
