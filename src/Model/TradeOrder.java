package Model;

import org.mozartspaces.capi3.Queryable;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by j0h1 on 11.04.2015.
 */
@Queryable(autoindex = true)
public class TradeOrder implements Serializable {

    private static final long serialVersionUID = 545149335657778572L;
    private String id;          // orderId
    private String investorId;  // investorId
    private String companyId;   // companyId of the wanted stocks company
    private Integer totalAmount; // total amount of stocks wanted
    private Integer completedAmount; // amount of stocks already "processed"
    private Double priceLimit;  // upper or lower price limit
    private Status status;
    private Type type;
    private InvestorType investorType;


    private TradeOrder(String investorId, Company companyOfStocksToBuyOrSell, Type type, Integer totalAmount, Double priceLimit) {
        this.investorId = investorId;
        this.companyId = companyOfStocksToBuyOrSell.getId();
        this.type = type;
        this.totalAmount = totalAmount;
        this.priceLimit = priceLimit;

        this.id = UUID.randomUUID().toString();
        this.completedAmount = 0;
        this.status = Status.OPEN;
    }

    public TradeOrder() {
        this.type = Type.ANY;
        this.status = Status.ANY;
    }

    public TradeOrder(Investor investor, Company companyOfStocksToBuyOrSell, Type type, Integer totalAmount, Double priceLimit) {
        this(investor.getId(),  companyOfStocksToBuyOrSell,type, totalAmount,priceLimit);
        this.investorType = InvestorType.INVESTOR;
    }

    public TradeOrder(Company investor, Company companyOfStocksToBuyOrSell, Integer totalAmount, Double priceLimit) {
        this(investor.getId(),  companyOfStocksToBuyOrSell,Type.SELL_ORDER, totalAmount,priceLimit);
        this.investorType = InvestorType.COMPANY;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInvestor(Company company) {
        this.investorId = company.getId();
    }

    public void setInvestor(Investor investor) {
        this.investorId = investor.getId();
        this.investorType = InvestorType.INVESTOR;
    }

    public void setCompany(Company company) {
        this.companyId = company.getId();
        this.investorType = InvestorType.COMPANY;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCompletedAmount(Integer completedAmount) {
        this.completedAmount = completedAmount;
    }

    public void setPriceLimit(Double priceLimit) {
        this.priceLimit = priceLimit;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setInvestorType(InvestorType investorType) {
        this.investorType = investorType;
    }

    public String getId() {
        return id;
    }

    public String getInvestorId() {
        return investorId;
    }

    public String getCompanyId() { return companyId; }

    public Company getCompany() {
        return new Company(companyId);
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

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status.equals(Status.ANY) || status.equals(Status.NOT_COMPLETED)) {
            return;
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return "TradeOrder{" +
                "id='" + id + '\'' +
                ", investorId='" + investorId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", totalAmount=" + totalAmount +
                ", completedAmount=" + completedAmount +
                ", priceLimit=" + priceLimit +
                ", status=" + status +
                ", type=" + type +
                ", investorType=" + investorType +
                '}';
    }

    public enum Status {
        //"Real" stati
        OPEN("open"),
        PARTIALLY_COMPLETED("partiallyCompleted"),
        COMPLETED("completed"),
        DELETED("deleted"),

        //For querying
        ANY("any"),
        NOT_COMPLETED("notCompleted");


        private final String text;

        /**
         * @param text
         */
        Status(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public enum Type {
        SELL_ORDER("sellOrder"),
        BUY_ORDER("buyOrder"),
        ANY("any");

        private final String text;

        /**
         * @param text
         */
        Type(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public enum InvestorType {
        COMPANY("company"),
        INVESTOR("investor");

        private final String text;

        /**
         * @param text
         */
        InvestorType(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }
}
