package Model;

import java.io.Serializable;

/**
 * Created by j0h1 on 27.04.2015.
 */
public class HistoryEntry implements Serializable {

    private static final long serialVersionUID = -9218736705222854815L;

    private String transactionId;
    private String brokerId;
    private Investor buyer;
    private StockOwner seller;
    private String stockName;
    private String buyOrderId;
    private String sellOrderId;
    private Double tradedMarketValue;
    private int amountOfStocks;
    private Double totalPrice;
    private Double provision;

    public HistoryEntry() {
    }

    public HistoryEntry(String transactionId, String brokerId, Investor buyer, StockOwner seller, String stockName,
                        String buyOrderId, String sellOrderId, Double tradedMarketValue, int amountOfStocks,
                        Double totalPrice, Double provision) {
        this.setTransactionId(transactionId);
        this.setBrokerId(brokerId);
        this.setBuyer(buyer);
        this.setSeller(seller);
        this.setStockName(stockName);
        this.setBuyOrderId(buyOrderId);
        this.setSellOrderId(sellOrderId);
        this.setTradedMarketValue(tradedMarketValue);
        this.setAmountOfStocks(amountOfStocks);
        this.setTotalPrice(totalPrice);
        this.setProvision(provision);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public Investor getBuyer() {
        return buyer;
    }

    public void setBuyer(Investor buyer) {
        this.buyer = buyer;
    }

    public StockOwner getSeller() {
        return seller;
    }

    public void setSeller(StockOwner seller) {
        this.seller = seller;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(String sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public Double getTradedMarketValue() {
        return tradedMarketValue;
    }

    public void setTradedMarketValue(Double tradedMarketValue) {
        this.tradedMarketValue = tradedMarketValue;
    }

    public int getAmountOfStocks() {
        return amountOfStocks;
    }

    public void setAmountOfStocks(int amountOfStocks) {
        this.amountOfStocks = amountOfStocks;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getProvision() {
        return provision;
    }

    public void setProvision(Double provision) {
        this.provision = provision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryEntry that = (HistoryEntry) o;

        return !(transactionId != null ? !transactionId.equals(that.transactionId) : that.transactionId != null);

    }

    @Override
    public int hashCode() {
        return transactionId != null ? transactionId.hashCode() : 0;
    }
}
