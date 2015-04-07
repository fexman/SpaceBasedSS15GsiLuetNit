package Model;

import java.io.Serializable;

/**
 * Created by Felix on 06.04.2015.
 */
public class Stock implements Serializable {

    private String companyId;
    private Integer amount;

    public Stock(String companyId, Integer amount) {
        this.companyId = companyId;
        this.amount = amount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public Integer getAmount() {
        return amount;
    }

    public IssueStockRequest toIssueStockRequest(Double price) {
        return new IssueStockRequest(companyId, amount, price);
    }

    public String toString() {
        return companyId+"-Stocks: #"+amount;
    }
}
