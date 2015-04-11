package Model;

import java.io.Serializable;

/**
 * Created by j0h1 on 11.04.2015.
 */
public abstract class TradeOrder implements Serializable {

    private static final long serialVersionUID = 545149335657778572L;
    private String id;          // orderId
    private String investorId;
    private String companyId;   // stockId
    private Integer totalAmount;
    private Integer completedAmount;
    private Double priceLimit;  // upper or lower price limit

    public String getId() {
        return id;
    }

    public String getInvestorId() {
        return investorId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public Integer getCompletedAmount() {
        return completedAmount;
    }

    public Double getPriceLimit() {
        return priceLimit;
    }

}
