package Model;

import org.mozartspaces.capi3.Queryable;

import java.io.Serializable;
import java.util.Date;
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
    private Integer openAmount;
    private Integer completedAmount; // amount of stocks already "processed"
    private Double priceLimit;  // upper or lower price limit
    private Status status;
    private Type type;
    private InvestorType investorType;
    private Boolean justChanged;
    private Long created;
    private Boolean prioritized;

    private TradeOrder(String investorId, Company companyOfStocksToBuyOrSell, Type type, Integer totalAmount, Double priceLimit) {
        this.investorId = investorId;
        this.companyId = companyOfStocksToBuyOrSell.getId();
        this.totalAmount = totalAmount;
        this.priceLimit = priceLimit;
        setType(type);

        this.id = UUID.randomUUID().toString();
        this.prioritized = false;
        this.completedAmount = 0;
        this.openAmount = totalAmount;
        this.justChanged = true;
        this.created = new Date().getTime();
        setStatus(Status.OPEN);
    }

    public TradeOrder() {
        setType(Type.ANY);
        setStatus(Status.ANY);
        this.created = new Date().getTime();
    }

    public TradeOrder(Investor investor, Company companyOfStocksToBuyOrSell, Type type, Integer totalAmount, Double priceLimit) {
        this(investor.getId(), companyOfStocksToBuyOrSell, type, totalAmount, priceLimit);
        this.investorType = InvestorType.INVESTOR;
        this.created = new Date().getTime();
    }

    public TradeOrder(Company investor, Company companyOfStocksToBuyOrSell, Integer totalAmount, Double priceLimit) {
        this(investor.getId(), companyOfStocksToBuyOrSell, Type.SELL_ORDER, totalAmount, priceLimit);
        this.investorType = InvestorType.COMPANY;
        this.created = new Date().getTime();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInvestor(Company company) {
        this.investorId = company.getId();
        this.investorType = InvestorType.COMPANY;
    }

    public void setInvestor(Investor investor) {
        this.investorId = investor.getId();
        this.investorType = InvestorType.INVESTOR;
    }

    public void setCompany(Company company) {
        this.companyId = company.getId();
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCompletedAmount(Integer completedAmount) {
        this.completedAmount = completedAmount;
        this.openAmount = totalAmount - completedAmount;
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

    public InvestorType getInvestorType() {
        return investorType;
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

    public Integer getPendingAmount() {
        return totalAmount - completedAmount;
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
                ", created=" + created +
                ", prioritized=" + prioritized +
                '}';
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Integer getOpenAmount() {
        return openAmount;
    }

    public void setOpenAmount(Integer openAmount) {
        this.openAmount = openAmount;
    }

    public Boolean getJustChanged() {
        return justChanged;
    }

    public void setJustChanged(Boolean justChanged) {
        this.justChanged = justChanged;
    }

    public Boolean isPrioritized() {
        return prioritized;
    }

    public void setPrioritized(Boolean prioritized) {
        this.prioritized = prioritized;
    }

    public enum Status {
        //"Real" stati
        OPEN("open"),
        PARTIALLY_COMPLETED("partiallyCompleted"),
        COMPLETED("completed"),
        DELETED("deleted"),

        //For querying
        ANY("any"),
        NOT_COMPLETED("notCompleted"),
        NOT_DELETED("notDeleted");


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
        SELL_ORDER("SELL"),
        BUY_ORDER("BUY"),
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
