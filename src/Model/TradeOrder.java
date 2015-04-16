package Model;

import MarketEntities.Depot;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by j0h1 on 11.04.2015.
 */
public abstract class TradeOrder implements Serializable {

    private static final long serialVersionUID = 545149335657778572L;
    private String id;          // orderId
    private String investorId;  // investorId
    private String companyId;   // companyId of te wanted stocks company
    private Integer totalAmount; // total amount of stocks wanted
    private Integer completedAmount; // amount of stocks already "processed"
    private Double priceLimit;  // upper or lower price limit
    private Status status;
    private Type type;

    public TradeOrder(String investorId, Company companyOfStocksToBuyOrSell, Type type, Integer totalAmount, Double priceLimit) {
        this.investorId = investorId;
        this.companyId = companyOfStocksToBuyOrSell.getId();
        this.type = type;
        this.totalAmount = totalAmount;
        this.priceLimit = priceLimit;

        this.id = UUID.randomUUID().toString();
        this.completedAmount = 0;
        this.status = Status.OPEN;

    }

    public String getId() {
        return id;
    }

    public String getInvestorId() {
        return investorId;
    }

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
        BUY_ORDER("buyOrder");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeOrder that = (TradeOrder) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
