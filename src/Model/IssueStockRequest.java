package Model;

import java.io.Serializable;

/**
 * Created by Felix on 06.04.2015.
 */
public class IssueStockRequest implements Serializable {


    private static final long serialVersionUID = 545149035659778572L;

    private String companyId;
    private double price;
    private int amount;

    public IssueStockRequest(String companyId, Integer amount, Double price) {
        this.companyId = companyId;
        this.price = price;
        this.amount = amount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return companyId+"-Stocks: #"+amount+" for "+price+",-";
    }

}
