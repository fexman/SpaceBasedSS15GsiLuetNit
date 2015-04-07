package Model;

import java.io.Serializable;

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

    public Stock toStock () {
        return new Stock(companyId, amount);
    }

    public Integer getAmount() {
        return amount;
    }

    public Double getPrice() {
        return price;
    }

    public String toString() {
        return companyId+"-Stocks: #"+amount+" for "+price+",-";
    }

}
